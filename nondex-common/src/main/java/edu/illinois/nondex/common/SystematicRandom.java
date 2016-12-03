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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.logging.Level;

public class SystematicRandom extends Random {
    public static int STARTING_COUNT = 59;
    private int replayIndex;
    private Stack<ExplorationEntry> choices;
    private String logFileName;
    private List<String> lines;
    private Configuration config = Configuration.parseArgs();

    public SystematicRandom() {
        logFileName = config.systematicLog;
        choices = new Stack<ExplorationEntry>();
        File file = new File(logFileName);
        if (file.exists()) {
            try {
                lines = Files.readAllLines(Paths.get(logFileName));
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.SEVERE,"Could not read lines from systematic.log" ,ioe);
            }
            Object[] choiceValues;
            for (String element: lines) {
                String delimiter = "[ ]+";
                if (!element.isEmpty()) {
                    choiceValues = element.split(delimiter);
                    if (choiceValues.length == 3) {
                        int current = Integer.parseInt(choiceValues[0].toString());
                        int maximum = Integer.parseInt(choiceValues[1].toString());
                        boolean shouldExplore = Boolean.parseBoolean(choiceValues[2].toString());
                        ExplorationEntry currentMaximum = new ExplorationEntry(current, maximum, shouldExplore);
                        choices.push(currentMaximum);
                    } else {
                        Logger.getGlobal().log(Level.SEVERE, "The 3 ExplorationEntry variable were not stored properly");
                    }

                }
            }
        }
    }

    public int nextInt(final int maximum) {
        int current;
        boolean explore;
        if (replayIndex < choices.size()) {
            current =  choices.get(replayIndex).getCurrent();
        } else {
            current = 0;
            if (choices.size() > STARTING_COUNT) {
                explore = true;
            } else {
                explore = false;
            }
            ExplorationEntry choiceNums = new ExplorationEntry(current, maximum, explore);
            choices.push(choiceNums);
        }
        replayIndex++;
        return current;
    }

    public void endRun() {
        while (!choices.isEmpty()) {
            ExplorationEntry currentMaximum = choices.pop();
            int current = currentMaximum.getCurrent();
            int maximum = currentMaximum.getMaximum();
            boolean shouldExplore = false;
            if (current < maximum - 1) {
                current++;
                if (choices.size() > STARTING_COUNT) {
                    shouldExplore = true;
                }
                ExplorationEntry lm = new ExplorationEntry(current, maximum, shouldExplore);
                choices.push(lm);
                replayIndex = 0;
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(logFileName))) {
                    for (ExplorationEntry element : choices) {
                        String lastAndMax = element.getCurrent() + " " + element.getMaximum()
                            + " " + element.getShouldExplore();
                        bufferedWriter.write(lastAndMax);
                        bufferedWriter.newLine();
                    }
                } catch (IOException ioe) {
                    Logger.getGlobal().log(Level.SEVERE,"Could not write systematic.log file" ,ioe);
                }
                return;
            }

            boolean allFalse = true;
            for (ExplorationEntry ch: choices) {
                if (ch.getShouldExplore()) {
                    allFalse = false;
                }
            }
            if (allFalse) {
                break;
            }
        }

        if (Files.exists(Paths.get(logFileName))) {
            try {
                Files.delete(Paths.get(logFileName));
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.WARNING,"Could not delete systematic.log file" ,ioe);
            }
        }
    }
}
