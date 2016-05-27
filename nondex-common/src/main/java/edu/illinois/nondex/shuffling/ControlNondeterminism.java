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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.ConfigurationDefaults;
import edu.illinois.nondex.common.Logger;

public class ControlNondeterminism {

    public static int count = 0;
    public static int shuffleCount = 0;

    private static final Logger logger = Logger.getGlobal();
    private static Random r;
    private static JVMShutdownHook jvmShutdownHook = new JVMShutdownHook();
    private static Configuration config = Configuration.parseArgs();

    static {
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(ControlNondeterminism.jvmShutdownHook);
        ControlNondeterminism.r = new Random(ControlNondeterminism.config.seed);
    }

    public static Configuration getConfiguration() {
        return ControlNondeterminism.config;
    }

    private static Random getRandomnessSource(int id, int modCount, int objHash, String source) {
        ControlNondeterminism.logger.log(Level.FINEST, "getRandomnessSource for API: " + source);
        if (!ControlNondeterminism.config.filter.matcher(source).matches()) {
            ControlNondeterminism.logger.log(Level.FINE, "Source does not apply " + source + "");
            return null;
            // Use null to denote do not randomize, only if passed
            // in source option and stack trace does not contain
        }

        switch (ControlNondeterminism.config.mode) {
            case FULL:
                //if (r == null) {
                //    r = new Random(config.SEED);
                //}
                return ControlNondeterminism.r;
            case ONE:
                return new Random(ControlNondeterminism.config.seed);
            case ID:
                return new Random(ControlNondeterminism.config.seed + id + modCount);
            case EQ:
                return new Random(ControlNondeterminism.config.seed + objHash);
            default:
                ControlNondeterminism.logger.log(Level.WARNING, "Unrecognized option for shuffle kind. Not shuffling.");
                return null;
        }
    }

    public static <T> List<T> shuffle(List<T> objs, int id, int modCount, int hash) {
        return ControlNondeterminism.internalShuffle(objs, id, modCount, hash, ControlNondeterminism.getSource());
    }

    public static <T> T[] shuffle(T[] objs, int hash) {
        if (objs == null) {
            return null;
        }

        List<T> ls = Arrays.asList(objs);
        ControlNondeterminism.internalShuffle(ls, hash, 0, hash, ControlNondeterminism.getSource());

        int index = 0;
        for (T l : ls) {
            objs[index++] = l;
        }
        return objs;

    }

    private static <T> List<T> internalShuffle(List<T> objs, int id, int modCount, int hash, String source) {
        Random currentRandom = ControlNondeterminism.getRandomnessSource(id, modCount, hash, source);
        // If randomness was null, that means do not shuffle
        if (currentRandom == null) {
            return objs;
        }
        List<T> ls = new ArrayList<T>(objs);
        Collections.shuffle(ls, currentRandom);

        // Determine if should return ordered or non-ordered
        if (ControlNondeterminism.shouldExploreForInstance()) {
            // If bounds are the same, then the one we want is when count is one
            // of those bounds
            if (ControlNondeterminism.config.start >= 0 && ControlNondeterminism.config.end >= 0
                    && ControlNondeterminism.config.start == ControlNondeterminism.config.end
                    && ControlNondeterminism.count == ControlNondeterminism.config.start) {
                StackTraceElement[] traces = Thread.currentThread().getStackTrace();
                for (StackTraceElement traceElement : traces) {
                    Logger.getGlobal().log(Level.CONFIG, "FOUND: " + traceElement.toString());
                }
            }
            ControlNondeterminism.count++;
            ControlNondeterminism.shuffleCount++;
            for (int i = 0; i < ls.size(); i++) {
                objs.set(i, ls.get(i));
            }
            return objs;
        } else {
            ControlNondeterminism.count++;
            return objs;
        }
    }

    private static boolean shouldExploreForInstance() {
        return ControlNondeterminism.count >= ControlNondeterminism.config.start
                && ControlNondeterminism.count <= ControlNondeterminism.config.end;
    }

    public static String[][] extendZoneStrings(String[][] strs) {
        ControlNondeterminism.logger.log(Level.FINEST, "extendZoneStrings");

        // passing 0 makes configs 1, 2, 3 the same thing.
        Random currentRandom = ControlNondeterminism.getRandomnessSource(0, 0, 0, ControlNondeterminism.getSource());

        // If randomness was null, that means do not shuffle
        if (currentRandom == null) {
            return strs;
        }
        if (currentRandom.nextBoolean()) {
            return strs;
        }
        if (ControlNondeterminism.shouldExploreForInstance()) {
            for (int i = 0; i < strs.length; i++) {
                strs[i] = Arrays.copyOf(strs[i], strs[i].length + 1);
            }
        }
        return strs;

    }

    private static String getSource() {
        return Thread.currentThread().getStackTrace()[3].toString();

    }

    private static class JVMShutdownHook extends Thread {
        @Override
        public void run() {
            ConfigurationDefaults.createNondexDirIfNeeded();
            try {
                Files.write(ControlNondeterminism.config.getConfigPath(),
                        ControlNondeterminism.config.toString().getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                Files.write(ControlNondeterminism.config.getInvocationsPath(),
                        ("COUNT:" + ControlNondeterminism.count + "\n").getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.write(ControlNondeterminism.config.getInvocationsPath(),
                        ("SHUFFLES:" + ControlNondeterminism.shuffleCount + "\n").getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
