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

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    int count;
    Stack<Object[]> choice;
    String logFileName;
    Charset utf8 = StandardCharsets.UTF_8;
    List<String> lines;

    public SystematicRandom() {
        logFileName = System.getenv("logFileName");
        if (logFileName != null) {
            File file = new File(this.logFileName);
            if (!file.exists()) {
                choice = new Stack<Object[]>();
                replayIndex = 0;
                count = 0;
            } else {
                choice = new Stack<Object[]>();
                try {
                    lines = Files.readAllLines(Paths.get(logFileName));
                } catch (IOException ioe) {
                    // TODO Auto-generated catch block
                    ioe.printStackTrace();
                }
                Object[] choiceValues = new Object[3];
                for (String element: lines) {
                    String delimit = "[ ]+";
                    if (!element.isEmpty()) {
                        choiceValues = element.split(delimit);
                        int last = Integer.parseInt(choiceValues[0].toString());
                        int max = Integer.parseInt(choiceValues[1].toString());
                        boolean explore = Boolean.parseBoolean((String) choiceValues[2]);
                        Object[] lastMax = { last, max, explore };
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

    public Stack<Object[]> getChoice() {
        return choice;
    }

    String getLogFileName() {
        return logFileName;
    }

    public int nextInt(int max) {
        int num;
        count++;
        boolean explore;
        if (replayIndex < choice.size()) {
            num = (int) choice.get(replayIndex)[0];
        } else {
            num = 0;
            if (count > 58) {
                explore = true;
            } else {
                explore = false;
            }
            Object[] choiceNums = { 0, max, explore };
            choice.push(choiceNums);
        }
        replayIndex++;
        return num;
    }

    public void endRun() throws IOException {
        while (!choice.isEmpty()) {
            Object[] lastMax = choice.pop();
            int last = (int) lastMax[0];
            int max = (int) lastMax[1];
            boolean explore = (boolean) lastMax[2];
            if (last < max - 1) {
                last++;
                Object[] lm = { last, max, true };
                choice.push(lm);
                replayIndex = 0;
                File file = new File(logFileName);
                if (file.exists()) {
                    file.delete();
                }
                BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(logFileName));
                for (Object[] element : choice) {
                    String lastAndMax = element[0] + " " + element[1] + " " + element[2];
                    bufferedWriter.write(lastAndMax);
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                return;
            }
        }
        Files.delete(Paths.get(logFileName));
    }
}
