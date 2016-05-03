/*
The MIT License (MIT)
Copyright (c) 2015 Alex Gyori
Copyright (c) 2015 Owolabi Legunsen
Copyright (c) 2015 Darko Marinov
Copyright (c) 2015 August Shi


Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package edu.illinois.nondex.shuffling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;

public class ControlNondeterminism {
    private static final Logger logger = Logger.getGlobal();
    private static Random r;

    private static Configuration config = Configuration.parseArgs();

    public static int count = 0;
    public static int shuffleCount = 0;
    private static JVMShutdownHook jvmShutdownHook = new JVMShutdownHook();

    static {
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
        r = new Random(config.seed);
    }

    public static Configuration getConfiguration() {
        return config;
    }

    private static Random getRandomnessSource(int id, int modCount, int objHash, String source) {
        logger.log(Level.FINEST, "getRandomnessSource for API: " + source);
        if (!config.filter.matcher(source).matches()) {
            logger.log(Level.FINE, "Source does not apply " + source + "");
            return null;
            // Use null to denote do not randomize, only if passed
            // in source option and stack trace does not contain
        }

        switch (config.mode) {
            case FULL:
                //if (r == null) {
                //    r = new Random(config.SEED);
                //}
                return r;
            case ONE:
                return new Random(config.seed);
            case ID:
                return new Random(config.seed + id + modCount);
            case EQ:
                return new Random(config.seed + objHash);
            default:
                logger.log(Level.WARNING, "Unrecognized option for shuffle kind. Not shuffling.");
                return null;
        }
    }

    public static <T> List<T> shuffle(List<T> objs, int id, int modCount, int hash) {
        return internalShuffle(objs, id, modCount, hash, getSource());
    }

    public static <T> T[] shuffle(T[] objs, int hash) {
        if (objs == null) {
            return null;
        }

        java.util.List<T> ls = Arrays.asList(objs);
        internalShuffle(ls, hash, 0, hash, getSource());

        int index = 0;
        for (T l : ls) {
            objs[index++] = l;
        }
        return objs;

    }

    private static <T> List<T> internalShuffle(List<T> objs, int id, int modCount, int hash, String source) {
        Random currentRandom = getRandomnessSource(id, modCount, hash, source);
        // If randomness was null, that means do not shuffle
        if (currentRandom == null) {
            return objs;
        }
        java.util.List<T> ls = new java.util.ArrayList<T>(objs);
        java.util.Collections.shuffle(ls, currentRandom);

        // Determine if should return ordered or non-ordered
        if (shouldExploreForInstance()) {
            // If bounds are the same, then the one we want is when count is one
            // of those bounds
            if (config.start >= 0 && config.end >= 0 && config.start == config.end && count == config.start) {
                StackTraceElement[] traces = Thread.currentThread().getStackTrace();
                for (StackTraceElement traceElement : traces) {
                    Logger.getGlobal().log(Level.CONFIG, "FOUND: " + traceElement.toString());
                }
            }
            count++;
            shuffleCount++;
            for (int i = 0; i < ls.size(); i++) {
                objs.set(i, ls.get(i));
            }
            return objs;
        } else {
            count++;
            return objs;
        }
    }

    private static boolean shouldExploreForInstance() {
        return count >= config.start && count <= config.end;
    }

    public static String[][] extendZoneStrings(String[][] strs) {
        logger.log(Level.FINEST, "extendZoneStrings");

        // passing 0 makes configs 1, 2, 3 the same thing.
        Random currentRandom = getRandomnessSource(0, 0, 0, getSource());

        // If randomness was null, that means do not shuffle
        if (currentRandom == null) {
            return strs;
        }
        if (currentRandom.nextBoolean()) {
            return strs;
        }
        if (shouldExploreForInstance()) {
            for (int i = 0; i < strs.length; i++) {
                strs[i] = java.util.Arrays.copyOf(strs[i], strs[i].length + 1);
            }
        }
        return strs;

    }

    private static String getSource() {
        return Thread.currentThread().getStackTrace()[3].toString();

    }

    private static class JVMShutdownHook extends Thread {
        public void run() {
            ConfigurationDefaults.createNondexDirIfNeeded();
            try {
                Files.write(config.getConfigPath(), config.toString().getBytes(), StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                Files.write(config.getInvocationsPath(), ("COUNT:" + ControlNondeterminism.count + "\n").getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.write(config.getInvocationsPath(),
                        ("SHUFFLES:" + ControlNondeterminism.shuffleCount + "\n").getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
