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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import edu.illinois.nondex.common.Configuration;
import edu.illinois.nondex.common.Logger;
import edu.illinois.nondex.common.NonDex;

public class ControlNondeterminism {

    private static JVMShutdownHook jvmShutdownHook = new JVMShutdownHook();

    private static NonDex nondex;
    private static List<Integer> lengths = new LinkedList<>();

    static {
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(ControlNondeterminism.jvmShutdownHook);
        nondex = new NonDex();
    }

    public static Configuration getConfiguration() {
        return nondex.getConfig();
    }

    public static <T> List<T> shuffle(List<T> originalOrder) {
        return shuffle(originalOrder, "");
    }
    public static <T> List<T> shuffle(List<T> originalOrder, String initTraces) {
        if (originalOrder.size() < 2) {
            return originalOrder;
        }
        lengths.add(originalOrder.size());
        return nondex.getPermutation(originalOrder, initTraces);
    }

    public static <T> T[] shuffle(T[] originalOrder) {
        if (originalOrder == null) {
            return null;
        }
        if (originalOrder.length < 2) {
            return originalOrder;
        }
        lengths.add(originalOrder.length);

        List<T> newOrder = Arrays.asList(originalOrder);

        newOrder = nondex.getPermutation(newOrder);

        newOrder.toArray(originalOrder);

        // return in place
        return originalOrder;
    }

    public static String[][] extendZoneStrings(String[][] originalArrays) {

        boolean shouldFlip = nondex.getBoolean();

        if (shouldFlip) {
            for (int i = 0; i < originalArrays.length; i++) {
                originalArrays[i] = Arrays.copyOf(originalArrays[i], originalArrays[i].length + 1);
            }
        }

        return originalArrays;

    }


    private static class JVMShutdownHook extends Thread {
        @Override
        public void run() {
            nondex.getConfig().createNondexDirIfNeeded();
            try {
                int localCount = nondex.getPossibleExplorations();
                int localShufflesCount = nondex.getActualExplorations();
                Files.write(nondex.getConfig().getConfigPath(),
                        nondex.getConfig().toString().getBytes(), StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
                Files.write(nondex.getConfig().getInvocationsPath(),
                        ("COUNT:" + localCount + String.format("%n")).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                Files.write(nondex.getConfig().getInvocationsPath(),
                        ("SHUFFLES:" + localShufflesCount + String.format("%n")).getBytes(),
                        StandardOpenOption.APPEND);
                Files.write(nondex.getConfig().getInvocationsPath(),
                            ("LENGTHS: " + ControlNondeterminism.lengths + String.format("%n")).getBytes(),
                            StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.SEVERE,
                        "IOException when printing shuffling counts in shutdown hook.", ioe);
                throw new RuntimeException(ioe);
            } catch (Throwable ex) {
                Logger.getGlobal().log(Level.SEVERE,
                        "Some Exception when printing shuffling counts in shutdown hook.", ex);
                throw ex;
            }
        }
    }
}
