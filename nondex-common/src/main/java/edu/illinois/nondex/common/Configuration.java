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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Configuration {

    public final Mode mode;
    public final int seed;
    public final Pattern filter;

    public final String executionId;

    public final long start;
    public final long end;

    public final boolean shouldPrintStackTrace;

    public final String nondexDir;
    public final String nondexJarDir;

    public final String testName;

    public final Level loggingLevel;

    private Integer invoCount = null;
    private Set<String> failedTests = null;

    protected Configuration(Mode mode, int seed, Pattern filter, String executionId) {
        this(mode, seed, filter, 0, Long.MAX_VALUE, ConfigurationDefaults.DEFAULT_NONDEX_DIR,
                ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR, null, executionId, Level.CONFIG);
    }

    public Configuration(Mode mode, int seed, Pattern filter, long start, long end, String nondexDir,
            String nondexJarDir, String testName, String executionId, Level loggingLevel) {
        this(mode, seed, filter, start, end, nondexDir, nondexJarDir, testName, executionId, loggingLevel, false);
    }

    public Configuration(Mode mode, int seed, Pattern filter, long start, long end, String nondexDir,
            String nondexJarDir, String testName, String executionId, Level loggingLevel, boolean printStackTrace) {
        this.mode = mode;
        this.seed = seed;
        this.filter = filter;
        this.start = start;
        this.end = end;
        this.nondexDir = nondexDir;
        this.nondexJarDir = nondexJarDir;
        this.testName = testName;
        this.executionId = executionId;
        this.shouldPrintStackTrace = printStackTrace;
        this.loggingLevel = loggingLevel;
        this.createExecutionDirIfNeeded();
    }


    public Configuration(String executionId, String nondexDir) {
        this(ConfigurationDefaults.DEFAULT_MODE, ConfigurationDefaults.DEFAULT_SEED,
                Pattern.compile(ConfigurationDefaults.DEFAULT_FILTER), 0, Long.MAX_VALUE,
                nondexDir, ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR,
                null, executionId, Logger.getGlobal().getLoggingLevel());
    }

    public void createNondexDirIfNeeded() {
        new File(this.nondexDir).mkdir();
    }

    public String toArgLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_FILTER + "=" + "\'" + this.filter + "\'");
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_MODE + "=" + this.mode);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_SEED + "=" + this.seed);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_START + "=" + this.start);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_END + "=" + this.end);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_PRINT_STACK + "=" + this.shouldPrintStackTrace);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_NONDEX_DIR + "=\"" + this.nondexDir + "\"");
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_NONDEX_JAR_DIR + "=\"" + this.nondexJarDir + "\"");
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + this.executionId);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_LOGGING_LEVEL + "=" + this.loggingLevel);
        sb.append(this.testName == null ? "" : " -Dtest=" + this.testName);
        return sb.toString();
    }

    @Override
    public String toString() {
        String[] props = new String[] {ConfigurationDefaults.PROPERTY_FILTER + "=" + this.filter,
                                       ConfigurationDefaults.PROPERTY_MODE + "=" + this.mode,
                                       ConfigurationDefaults.PROPERTY_SEED + "=" + this.seed,
                                       ConfigurationDefaults.PROPERTY_START + "=" + this.start,
                                       ConfigurationDefaults.PROPERTY_END + "=" + this.end,
                                       ConfigurationDefaults.PROPERTY_PRINT_STACK + "=" + this.shouldPrintStackTrace,
                                       ConfigurationDefaults.PROPERTY_NONDEX_DIR + "=" + this.nondexDir,
                                       ConfigurationDefaults.PROPERTY_NONDEX_JAR_DIR + "=" + this.nondexJarDir,
                                       ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + this.executionId,
                                       ConfigurationDefaults.PROPERTY_LOGGING_LEVEL + "=" + this.loggingLevel,
                                       "test=" + (this.testName == null ? "" : this.testName)};
        return String.join(String.format("%n"), props);
    }

    public static Configuration parseArgs() {
        return Configuration.parseArgs(System.getProperties());
    }

    public static Configuration parseArgs(Properties props) {
        final String executionId = props.getProperty(ConfigurationDefaults.PROPERTY_EXECUTION_ID,
                ConfigurationDefaults.NO_EXECUTION_ID);

        final int seed = Integer.parseInt(props.getProperty(ConfigurationDefaults.PROPERTY_SEED,
                ConfigurationDefaults.DEFAULT_SEED_STR));

        final Mode nonDetKind = Mode.valueOf(props.getProperty(ConfigurationDefaults.PROPERTY_MODE,
                ConfigurationDefaults.DEFAULT_MODE_STR));

        final Pattern filter = Pattern.compile(props.getProperty(ConfigurationDefaults.PROPERTY_FILTER,
                ConfigurationDefaults.DEFAULT_FILTER));

        final long start = Long.parseLong(props.getProperty(ConfigurationDefaults.PROPERTY_START,
                ConfigurationDefaults.DEFAULT_START_STR));

        final long end = Long.parseLong(
                props.getProperty(ConfigurationDefaults.PROPERTY_END, ConfigurationDefaults.DEFAULT_END_STR));

        final boolean shouldPrintStacktrace = Boolean.parseBoolean(
                props.getProperty(ConfigurationDefaults.PROPERTY_PRINT_STACK,
                        ConfigurationDefaults.DEFAULT_PRINT_STACK_STR));


        final String nondexDir = props.getProperty(ConfigurationDefaults.PROPERTY_NONDEX_DIR,
                ConfigurationDefaults.DEFAULT_NONDEX_DIR);

        final String nondexJarDir = props.getProperty(ConfigurationDefaults.PROPERTY_NONDEX_JAR_DIR,
                ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR);

        final Level level = Level.parse(props.getProperty(
                ConfigurationDefaults.PROPERTY_LOGGING_LEVEL, ConfigurationDefaults.DEFAULT_LOGGING_LEVEL));
        Logger.getGlobal().setLoggingLevel(level);

        final String testName = props.getProperty("test", null);

        return new Configuration(nonDetKind, seed, filter, start, end, nondexDir, nondexJarDir, testName,
                executionId, level, shouldPrintStacktrace);
    }

    public void createExecutionDirIfNeeded() {
        Paths.get(this.nondexDir, this.executionId).toFile().mkdirs();
    }

    public Path getNondexDir() {
        return Paths.get(this.nondexDir, this.executionId);
    }

    public Path getExecutionDir() {
        return Paths.get(this.nondexDir, this.executionId);
    }

    public Path getFailuresPath() {
        return Paths.get(this.nondexDir, this.executionId, ConfigurationDefaults.FAILURES_FILE);
    }

    public Path getInvocationsPath() {
        return Paths.get(this.nondexDir, this.executionId, ConfigurationDefaults.INVOCATIONS_FILE);
    }

    public Path getDebugPath() {
        return Paths.get(this.nondexDir, this.executionId, ConfigurationDefaults.DEBUG_FILE);
    }

    public Path getConfigPath() {
        return Paths.get(this.nondexDir, this.executionId, ConfigurationDefaults.CONFIGURATION_FILE);
    }

    public Path getRunFilePath() {
        return Paths.get(this.nondexDir, this.executionId + ".run");
    }

    public Path getLatestRunFilePath() {
        return Paths.get(this.nondexDir, ConfigurationDefaults.LATEST_RUN_ID);
    }

    public Path getPathToJar() {
        return Paths.get(this.nondexJarDir, ConfigurationDefaults.INSTRUMENTATION_JAR);
    }

    public int getInvocationCount() {
        if (this.invoCount == null) {
            File failed = Paths.get(this.nondexDir, this.executionId,
                    ConfigurationDefaults.INVOCATIONS_FILE).toFile();

            try (BufferedReader br = new BufferedReader(new FileReader(failed))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("SHUFFLES:")) {
                        this.invoCount = new Integer(line.substring("SHUFFLES: ".length() - 1));
                    }
                }
            } catch (FileNotFoundException fne) {
                Logger.getGlobal().log(Level.FINEST, "File Not Found. Probably no test failed in this run.");
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.WARNING, "Exception reading failures file.", ioe);
            } catch (Throwable thr) {
                Logger.getGlobal().log(Level.SEVERE, "Some big error", thr);
            }
        }
        return this.invoCount;
    }

    public void filterTests(Collection<String> failedInClean) {
        Collection<String> failedTestsInExecution = this.getFailedTests();

        failedTestsInExecution = new LinkedHashSet<String>(failedTestsInExecution);
        failedTestsInExecution.removeAll(failedInClean);

        this.printFailuresToFile(failedTestsInExecution);
        this.failedTests = null;
    }

    private void printFailuresToFile(Collection<String> failedTestsInExecution) {
        File failed = Paths.get(this.nondexDir, this.executionId, ConfigurationDefaults.FAILURES_FILE)
                    .toFile();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(failed))) {
            for (String test : failedTestsInExecution) {
                bw.write(test + String.format("%n"));
            }
        } catch (FileNotFoundException fne) {
            Logger.getGlobal().log(Level.FINEST, "File Not Found. Probably no test failed in this run.");
        } catch (IOException ioe) {
            Logger.getGlobal().log(Level.WARNING, "Exception reading failures file.", ioe);
        }
    }

    public Collection<String> getFailedTests() {
        if (this.failedTests == null) {
            this.failedTests = new LinkedHashSet<>();
            File failed = Paths.get(this.nondexDir, this.executionId, ConfigurationDefaults.FAILURES_FILE)
                    .toFile();

            try (BufferedReader br = new BufferedReader(new FileReader(failed))) {
                String line;
                while ((line = br.readLine()) != null) {
                    this.failedTests.add(line.trim());
                }
            } catch (FileNotFoundException fne) {
                Logger.getGlobal().log(Level.FINEST, "File Not Found. Probably no test failed in this run.");
            } catch (IOException ioe) {
                Logger.getGlobal().log(Level.WARNING, "Exception reading failures file.", ioe);
            }
        }
        return Collections.unmodifiableCollection(this.failedTests);
    }

    public long numChoices() {
        assert (this.end >= this.start);
        return this.end - this.start;
    }

    public boolean hasFewerChoicePoints(Configuration debConfig) {
        return (this.numChoices() < debConfig.numChoices());
    }

    public void setFailures(Set<String> failingTests) {
        this.printFailuresToFile(failingTests);
    }
}
