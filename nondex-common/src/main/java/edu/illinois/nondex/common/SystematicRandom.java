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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class SystematicRandom extends Random {
    public static int STARTING_COUNT = 59;
    int replayIndex;
    int count;
    Stack<StackElement> choice;
    String logFileName;
    Charset utf8 = StandardCharsets.UTF_8;
    List<String> lines;

    public SystematicRandom() {
        logFileName = System.getenv("logFileName");
        if (logFileName != null) {
            File file = new File(this.logFileName);
            if (!file.exists()) {
                choice = new Stack<StackElement>();
                replayIndex = 0;
                count = 0;
            } else {
                choice = new Stack<StackElement>();
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
                        StackElement lastMax = new StackElement( last, max, explore );
                        choice.push(lastMax);
                    }
                }
            }
        } else {
            System.err.println("The env variable does not exist. File could not be created");
            System.exit(1);
        }
    }

    public int nextInt(int max) {
        int last;
        count++;
        boolean explore;
        if (replayIndex < choice.size()) {
            last =  choice.get(replayIndex).getLast();
        } else {
            last = 0;
            if (count > STARTING_COUNT + 1) {
                explore = true;
            } else {
                explore = false;
            }
            StackElement choiceNums = new StackElement(last, max, explore);
            choice.push(choiceNums);
        }
        replayIndex++;
        return last;
    }

    public void endRun() throws IOException {
        while (!choice.isEmpty()) {
            ArrayList<Object> ex = new ArrayList<>();
            StackElement lastMax = choice.pop();
            int last = lastMax.getLast();
            int max = lastMax.getMax();
            boolean explore = false;
            if (last < max - 1) {
                last++;
                if (choice.size() > STARTING_COUNT) {
                    explore = true;
                }
                StackElement lm = new StackElement(last, max, explore);
                choice.push(lm);
                replayIndex = 0;
                if (Files.exists(Paths.get(logFileName))) {
                    Files.delete(Paths.get(logFileName));
                }
                BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(logFileName));
                for (StackElement element : choice) {
                    String lastAndMax = element.getLast() + " " + element.getMax() + " " + element.getExplore();
                    bufferedWriter.write(lastAndMax);
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                return;
            }
            for (StackElement ch: choice) {
                ex.add(ch.getExplore());
            }
            if (!ex.contains(true)) {
                break;
            }
        }
        Files.delete(Paths.get(logFileName));
    }
}
