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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;


public class SystematicRandom extends Random {
    int replayIndex;
    Stack<int[]> choice;
    String logFileName;
    Charset utf8 = StandardCharsets.UTF_8;
    List<String> lines;

    public SystematicRandom() {
        logFileName = System.getenv("logFileName");
        if (logFileName != null) {
            File file = new File(this.logFileName);
            System.err.println(file.exists());
            if (!file.exists()) {
                choice = new Stack<int[]>();
                replayIndex = 0;
            } else {
                choice = new Stack<int[]>();
                try {
                    lines = Files.readAllLines(Paths.get(logFileName));
                } catch (IOException ioe) {
                    // TODO Auto-generated catch block
                    ioe.printStackTrace();
                }
                int lineSize = lines.size();
                String[] choiceValues = new String[2];
                for (int count = 0; count < lineSize; count++) {
                    String delimit = "[ ]+";
                    if (!lines.get(count).isEmpty()) {
                        choiceValues = lines.get(count).split(delimit);
                        int last = Integer.parseInt(choiceValues[0]);
                        int max = Integer.parseInt(choiceValues[1]);
                        int[] lastMax = { last, max };
                        choice.push(lastMax);
                    }
                }
            }
        } else {
            System.err.println("The env variable does not exist. File could not be created");
            System.exit(1);
        }
    }

    int getReplayIndex() {
        return replayIndex;
    }

    public Stack<int[]> getChoice() {
        return choice;
    }

    String getLogFileName() {
        return logFileName;
    }

    public int nextInt(int max) {
        int num;
        if (replayIndex < choice.size()) {
            num = choice.get(replayIndex)[0];
        } else {
            num = 0;
            int[] choiceNums = { 0, max };
            choice.push(choiceNums);
        }
        replayIndex++;
        return num;
    }

    public void endRun() throws IOException {
        System.err.println("CALLEDDDDD");
        while (!choice.isEmpty()) {
            int[] lastMax = choice.pop();
            int last = lastMax[0];
            int max = lastMax[1];
            if (last < max - 1) {
                last++;
                int[] lm = { last, max };
                choice.push(lm);
                replayIndex = 0;
                File file = new File(this.logFileName);
                if (file.exists()) {
                    file.delete();
                }
                for (int count = 0; count < choice.size(); count++) {
                    String last0 = new Integer(choice.get(count)[0]).toString();
                    String max0 = new Integer(choice.get(count)[1]).toString();
                    String lastMax0 = last0.concat(" ").concat(max0);
                    List<String> lastAndMax = Arrays.asList(lastMax0);
                    Files.write(Paths.get(this.logFileName), lastAndMax, utf8, StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);
                }
                return;
            }
        }
        File file = new File(this.logFileName);
        file.delete();
    }
}
