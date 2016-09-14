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

    public static <T> List<T> shuffle(List<T> originalOrder) {
        return ControlNondeterminism.internalShuffle(originalOrder, ControlNondeterminism.getSource());
    }

    public static <T> T[] shuffle(T[] originalOrder) {
        if (originalOrder == null) {
            return null;
        }

        List<T> newOrder = Arrays.asList(originalOrder);
        newOrder = ControlNondeterminism.internalShuffle(newOrder, ControlNondeterminism.getSource());
        newOrder.toArray(originalOrder);

        // return in place
        return originalOrder;
    }

    public static String[][] extendZoneStrings(String[][] originalArrays) {
        ControlNondeterminism.logger.log(Level.FINEST, "extendZoneStrings");

        // If in state of outputting, do not do any shuffling and other stuff
        if (!ControlNondeterminism.shouldOutputTrace) {
            return originalArrays;
        }

        Random currentRandom = ControlNondeterminism.getRandomnessSource(ControlNondeterminism.getSource());

        // If randomness was null, that means do not shuffle
        if (currentRandom == null) {
            return originalArrays;
        }

        boolean shouldFlip = currentRandom.nextBoolean();

        // Determine if should return extended or non-extended
        if (ControlNondeterminism.shouldExploreForInstance()) {
            ControlNondeterminism.printStackTraceIfUniqueDebugPoint();
            ControlNondeterminism.shuffleCount++;
            // By flip of coin, determine if should extend array or not
            if (shouldFlip) {
                for (int i = 0; i < originalArrays.length; i++) {
                    originalArrays[i] = Arrays.copyOf(originalArrays[i], originalArrays[i].length + 1);
                }
            }
        }
        ControlNondeterminism.count++;
        return originalArrays;

    }

    private static <T> List<T> internalShuffle(List<T> originalOrder, String source) {
        // If in state of outputting, do not do any shuffling and other stuff
        if (!ControlNondeterminism.shouldOutputTrace) {
            return originalOrder;
        }

        // If size of collection to shuffle has at most one element, no need to shuffle
        if (originalOrder.size() <= 1) {
            return originalOrder;
        }

        Random currentRandom = ControlNondeterminism.getRandomnessSource(source);
        // If randomness was null, that means do not shuffle
        if (currentRandom == null) {
            return originalOrder;
        }

        List<T> newOrder = new ArrayList<T>(originalOrder);
        Collections.shuffle(newOrder, currentRandom);

        // Determine if should return ordered or non-ordered
        if (ControlNondeterminism.shouldExploreForInstance()) {
            ControlNondeterminism.printStackTraceIfUniqueDebugPoint();
            ControlNondeterminism.count++;
            ControlNondeterminism.shuffleCount++;
            return newOrder;
        } else {
            ControlNondeterminism.count++;
            return originalOrder;
        }
    }

    private static String trimStackTrace(String stackstring) {
        String[] lines = stackstring.split("\n");
        StringBuilder prettyStackString = new StringBuilder();
        boolean endOfStackTrace = false;
        for (String line : lines) {
            if (line.startsWith("TEST")
                    || line.startsWith("java.lang.Thread.getStackTrace")
                    || line.startsWith("edu.illinois.nondex.shuffling")) {
                continue;
            }
            if (!endOfStackTrace) {
                prettyStackString.append(line);
                prettyStackString.append("\n");
            }
            if (line.startsWith(ControlNondeterminism.config.testName.replace("#", "."))) {
                endOfStackTrace = true;
            }

        }
        return prettyStackString.toString();
    }

    private static void printStackTraceIfUniqueDebugPoint() {
        if (ControlNondeterminism.config.shouldPrintStackTrace && ControlNondeterminism.isDebuggingUniquePoint()) {
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            StringBuilder stackstring = new StringBuilder();
            for (StackTraceElement traceElement : traces) {
                stackstring.append(traceElement.toString() + String.format("%n"));
            }
            try {
                // Writing to file invokes NonDex, so this flag is to prevent it from infinitely trying to write to file,
                // and to prevent it from doing other things when all we want is to print out a stack trace
                ControlNondeterminism.shouldOutputTrace = false;
                Files.write(ControlNondeterminism.config.getDebugPath(),
                            ("TEST: " + ControlNondeterminism.config.testName + String.format("%n") + stackstring.toString())
                            .getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.SEVERE, "Exception when printing debug info.", ioe);
            } finally {
                ControlNondeterminism.shouldOutputTrace = true;
            }

            Logger.getGlobal().log(Level.SEVERE, trimStackTrace(stackstring.toString()));
        }
    }

    private static boolean isDebuggingUniquePoint() {
        return ControlNondeterminism.config.start >= 0 && ControlNondeterminism.config.end >= 0
                && ControlNondeterminism.config.start == ControlNondeterminism.config.end
                && ControlNondeterminism.count == ControlNondeterminism.config.start;
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
                        ("COUNT:" + localCount + String.format("%n")).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.write(ControlNondeterminism.config.getInvocationsPath(),
                        ("SHUFFLES:" + localShufflesCount + String.format("%n")).getBytes(),
                        StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.SEVERE, "Exception when printing shuflling counts in shutdown hook.", ioe);
            }
        }
    }
}
