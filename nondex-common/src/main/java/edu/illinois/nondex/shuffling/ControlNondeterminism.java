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

    private static int count = 0;
    private static int shuffleCount = 0;

    private static final Logger logger = Logger.getGlobal();
    private static Random r;
    private static JVMShutdownHook jvmShutdownHook = new JVMShutdownHook();
    private static Configuration config = Configuration.parseArgs();

    private static boolean shouldOutputTrace = true;

    static {
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(ControlNondeterminism.jvmShutdownHook);
        ControlNondeterminism.r = new Random(ControlNondeterminism.config.seed);
    }

    public static Configuration getConfiguration() {
        return ControlNondeterminism.config;
    }

    private static Random getRandomnessSource(String source) {
        ControlNondeterminism.logger.log(Level.FINEST, "getRandomnessSource for API: " + source);
        if (!ControlNondeterminism.config.filter.matcher(source).matches()) {
            ControlNondeterminism.logger.log(Level.FINE, "Source does not apply " + source + "");
            // Use null to denote do not randomize, only if passed
            // in source option and stack trace does not contain
            return null;
        }

        switch (ControlNondeterminism.config.mode) {
            case FULL:
                return ControlNondeterminism.r;
            case ONE:
                return new Random(ControlNondeterminism.config.seed);
            default:
                ControlNondeterminism.logger.log(Level.WARNING, "Unrecognized option for shuffle kind. Not shuffling.");
                return null;
        }
    }

    public static <T> List<T> shuffle(List<T> objs) {
        return ControlNondeterminism.internalShuffle(objs, ControlNondeterminism.getSource());
    }

    public static <T> T[] shuffle(T[] objs) {
        if (objs == null) {
            return null;
        }

        List<T> ls = Arrays.asList(objs);
        ls = ControlNondeterminism.internalShuffle(ls, ControlNondeterminism.getSource());
        ls.toArray(objs);

        return objs;
    }

    public static String[][] extendZoneStrings(String[][] strs) {
        ControlNondeterminism.logger.log(Level.FINEST, "extendZoneStrings");

        Random currentRandom = ControlNondeterminism.getRandomnessSource(ControlNondeterminism.getSource());

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

    private static <T> List<T> internalShuffle(List<T> objs, String source) {
        Random currentRandom = ControlNondeterminism.getRandomnessSource(source);
        // If randomness was null, that means do not shuffle
        if (currentRandom == null) {
            return objs;
        }

        List<T> ls = new ArrayList<T>(objs);
        Collections.shuffle(ls, currentRandom);

        // Determine if should return ordered or non-ordered
        if (ControlNondeterminism.shouldExploreForInstance()) {
            // TODO(gyori): Communicate this stack trace in a better way
            if (ControlNondeterminism.config.start >= 0 && ControlNondeterminism.config.end >= 0
                    && ControlNondeterminism.config.start == ControlNondeterminism.config.end
                    && ControlNondeterminism.count == ControlNondeterminism.config.start) {
                StackTraceElement[] traces = Thread.currentThread().getStackTrace();
                StringBuilder stackstring = new StringBuilder();
                for (StackTraceElement traceElement : traces) {
                    //Logger.getGlobal().log(Level.CONFIG, "FOUND: " + traceElement.toString());
                    stackstring.append(traceElement.toString() + "\n");
                }
                try {
                    // Writing to file invokes NonDex, so this flag is to prevent it from infinitely trying to write to file
                    if (shouldOutputTrace) {
                        shouldOutputTrace = false;
                        Files.write(ControlNondeterminism.config.getDebugPath(),
                            ("TEST: " + ControlNondeterminism.config.testName + "\n" + stackstring.toString()).getBytes(),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    }
                    shouldOutputTrace = true;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            ControlNondeterminism.count++;
            ControlNondeterminism.shuffleCount++;

            return ls;
        } else {
            ControlNondeterminism.count++;
            return objs;
        }
    }

    private static boolean shouldExploreForInstance() {
        return ControlNondeterminism.count >= ControlNondeterminism.config.start
                && ControlNondeterminism.count <= ControlNondeterminism.config.end;
    }

    private static String getSource() {
        return Thread.currentThread().getStackTrace()[3].toString();

    }

    private static class JVMShutdownHook extends Thread {
        @Override
        public void run() {
            ControlNondeterminism.config.createNondexDirIfNeeded();
            try {
                int localCount = ControlNondeterminism.count;
                int localShufflesCount = ControlNondeterminism.shuffleCount;
                Files.write(ControlNondeterminism.config.getConfigPath(),
                        ControlNondeterminism.config.toString().getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                Files.write(ControlNondeterminism.config.getInvocationsPath(),
                        ("COUNT:" + localCount + "\n").getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.write(ControlNondeterminism.config.getInvocationsPath(),
                        ("SHUFFLES:" + localShufflesCount + "\n").getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
