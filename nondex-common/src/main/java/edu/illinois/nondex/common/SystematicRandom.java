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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class SystematicRandom extends Random {
    public static int STARTING_COUNT = 59;
    int replayIndex;
    Stack<ExplorationEntry> choices;
    String logFileName;
    List<String> lines;

    public SystematicRandom() {
        logFileName = ConfigurationDefaults.DEFAULT_LOG_STR;
        File file = new File(logFileName);
        if (!file.exists()) {
            choices = new Stack<ExplorationEntry>();
            replayIndex = 0;
        } else {
            choices = new Stack<ExplorationEntry>();
            try {
                lines = Files.readAllLines(Paths.get(logFileName));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            Object[] choiceValues = new Object[3];
            for (String element: lines) {
                String delimit = "[ ]+";
                if (!element.isEmpty()) {
                    choiceValues = element.split(delimit);
                    int last = Integer.parseInt(choiceValues[0].toString());
                    int max = Integer.parseInt(choiceValues[1].toString());
                    boolean explore = Boolean.parseBoolean(choiceValues[2].toString());
                    ExplorationEntry lastMax = new ExplorationEntry(last, max, explore);
                    choices.push(lastMax);
                }
            }
        }
    }

    public int nextInt(int max) {
        int last;
        boolean explore;
        if (replayIndex < choices.size()) {
            last =  choices.get(replayIndex).getCurrent();
        } else {
            last = 0;
            if (choices.size() > STARTING_COUNT) {
                explore = true;
            } else {
                explore = false;
            }
            ExplorationEntry choiceNums = new ExplorationEntry(last, max, explore);
            choices.push(choiceNums);
        }
        replayIndex++;
        return last;
    }

    public void endRun() throws IOException {
        while (!choices.isEmpty()) {
            ArrayList<Object> ex = new ArrayList<>();
            ExplorationEntry lastMax = choices.pop();
            int last = lastMax.getCurrent();
            int max = lastMax.getMaximum();
            boolean explore = false;
            if (last < max - 1) {
                last++;
                if (choices.size() > STARTING_COUNT) {
                    explore = true;
                }
                ExplorationEntry lm = new ExplorationEntry(last, max, explore);
                choices.push(lm);
                replayIndex = 0;
                if (Files.exists(Paths.get(logFileName))) {
                    Files.delete(Paths.get(logFileName));
                }
                BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(logFileName));
                for (ExplorationEntry element : choices) {
                    String lastAndMax = element.getCurrent() + " " + element.getMaximum() + " " + element.getShouldExplore();
                    bufferedWriter.write(lastAndMax);
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                return;
            }
            for (ExplorationEntry ch: choices) {
                ex.add(ch.getShouldExplore());
            }
            if (!ex.contains(true)) {
                break;
            }
        }
        Files.delete(Paths.get(logFileName));
    }
}
