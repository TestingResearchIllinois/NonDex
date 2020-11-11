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

package edu.illinois.nondex.common;

import jdk.internal.misc.VM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class NonDex {

    private static final NonDex globalInstance = new NonDex();

    private int opportunityCount;
    private int actualCount;

    private final Configuration config;
    private final Random        randomNumber;

    private boolean isOutputting;

    public NonDex() {
        this(Configuration.parseArgs());
    }

    public NonDex(Configuration config) {
        this.opportunityCount = 0;
        this.actualCount = 0;

        this.config = config;
        this.randomNumber = new Random(config.seed);

        this.isOutputting = false;
    }

    public static NonDex getInstance() {
        return globalInstance;
    }

    public boolean getBoolean() {

        return this.getBoolean(false);
    }

    public boolean getBoolean(boolean identityElement) {

        boolean result = this.getRandom().nextBoolean();
        if (this.shouldExplore()) {
            return result;
        }

        return identityElement;
    }

    public int getInteger() {

        return this.getInteger(0);
    }

    public int getInteger(int identityElement) {

        int result = this.getRandom().nextInt();
        if (this.shouldExplore()) {
            return result;
        }

        return identityElement;
    }

    public <T> List<T> getPermutation(List<T> originalOrder) {

        if (originalOrder == null) {
            throw new IllegalArgumentException("originalOrder is null");
        }
        if (this.shouldExplore()) {
            Collections.shuffle(originalOrder, this.getRandom());
        } else {
            // shuffle just to advance the PRNG
            Collections.shuffle(new ArrayList<T>(originalOrder), this.getRandom());
        }
        return originalOrder;
    }

    private Random getRandom() {
        Logger.getGlobal().log(Level.FINEST, "getRandomnessSource");

        switch (this.config.mode) {
            case FULL:
                return this.randomNumber;
            case ONE:
                return new Random(this.config.seed);
            default:
                Logger.getGlobal().log(Level.SEVERE,
                    "Unrecognized option for shuffle kind. Not shuffling.");
                return null;
        }
    }

    private boolean shouldExplore() {
        this.opportunityCount++;
        // When outputting, it should not explore behaviors, since then we output some debug info
        // and that should be ordered
        if (!this.isOutputting && this.shouldExploreForInstance() && this.apiShouldBeExplored()) {
            Logger.getGlobal().log(Level.FINE, "Exploring for current source");
            printStackTraceIfUniqueDebugPoint();
            this.actualCount++;
            return true;
        }
        Logger.getGlobal().log(Level.FINE, "NOT Exploring for current source");
        return false;
    }

    private boolean apiShouldBeExplored() {
        return config.filter.matcher(this.getInvocationElement()).matches();
    }

    private String getInvocationElement() {
        // We need to skip here the first element of the stack since that is in the Thread class
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : Arrays.asList(stack).subList(1, stack.length - 1)) {
            if (!el.getClassName().startsWith("edu.illinois.nondex")) {
                Logger.getGlobal().log(Level.FINE, "The invocation element is: " + el.toString());
                return el.toString();
            }
        }
        Logger.getGlobal().log(Level.SEVERE,
                "There is only edu.illinois.nondex on the stack trace or the stack trace is empty");
        return "UNKNOWN";
    }

    private void printStackTraceIfUniqueDebugPoint() {
        if (config.shouldPrintStackTrace
                && this.isDebuggingUniquePoint()) {
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            StringBuilder stackstring = new StringBuilder();
            for (StackTraceElement traceElement : traces) {
                stackstring.append(traceElement.toString() + String.format("%n"));
            }
            try {
                // Writing to file invokes NonDex, so this flag is to prevent it from infinitely
                // trying to write to file,
                // and to prevent it from doing other things when all we want is to print out a
                // stack trace
                this.isOutputting = true;
                Files.write(this.config.getDebugPath(),
                        ("TEST: " + this.config.testName + String.format("%n")
                                + stackstring.toString()).getBytes(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.SEVERE, "Exception when printing debug info.", ioe);
            } finally {
                this.isOutputting = false;
            }
        }
    }

    private boolean isDebuggingUniquePoint() {
        return config.start >= 0 && config.end >= 0
                && config.start == config.end
                && this.getPossibleExplorations() == config.start;
    }


    private boolean shouldExploreForInstance() {
        return this.opportunityCount >= this.config.start
                && this.opportunityCount <= this.config.end;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public int getPossibleExplorations() {
        return this.opportunityCount;
    }

    public int getActualExplorations() {
        return this.actualCount;
    }
}
