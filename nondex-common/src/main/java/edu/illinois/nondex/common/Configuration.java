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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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

    public final String testName;

    private Integer invoCount = null;
    private Set<String> failedTests = null;

    public Configuration(Mode mode, int seed, Pattern filter, String executionId) {
        this(mode, seed, filter, 0, Long.MAX_VALUE, null, executionId);
    }

    public Configuration(Mode mode, int seed, Pattern filter, long start, long end, String testName,
            String executionId) {
        this.mode = mode;
        this.seed = seed;
        this.filter = filter;
        this.start = start;
        this.end = end;
        this.testName = testName;
        this.executionId = executionId;
        this.createExecutionDirIfNeeded();
    }

    public String toArgLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_FILTER + "=" + "\'" + this.filter + "\'");
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_MODE + "=" + this.mode);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_SEED + "=" + this.seed);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_START + "=" + this.start);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_END + "=" + this.end);
        sb.append(" -D" + ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + this.executionId);
        sb.append(this.testName == null ? "" : " -Dtest=" + this.testName);
        return sb.toString();
    }

    public String toString() {
        return ConfigurationDefaults.PROPERTY_FILTER + "=" + this.filter + "\n"
                + ConfigurationDefaults.PROPERTY_MODE + "=" + this.mode + "\n"
                + ConfigurationDefaults.PROPERTY_SEED + "=" + this.seed + "\n"
                + ConfigurationDefaults.PROPERTY_START + "=" + this.start + "\n"
                + ConfigurationDefaults.PROPERTY_END + "=" + this.end + "\n"
                + ConfigurationDefaults.PROPERTY_EXECUTION_ID + "=" + this.executionId + "\n"
                + "test=" + (this.testName == null ? "" : this.testName);
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

        final Level level = Level.parse(props.getProperty(
                ConfigurationDefaults.PROPERTY_LOGGING_LEVEL, ConfigurationDefaults.DEFAULT_LOGGING_LEVEL));
        Logger.getGlobal().setLoggineLevel(level);

        final String testName = props.getProperty("test", null);

        return new Configuration(nonDetKind, seed, filter, start, end, testName, executionId);
    }

    public void createExecutionDirIfNeeded() {
        Paths.get(ConfigurationDefaults.NONDEX_DIR, this.executionId).toFile().mkdirs();
    }

    public Path getFailuresPath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, this.executionId, ConfigurationDefaults.FAILURES_FILE);
    }

    public Path getInvocationsPath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, this.executionId, ConfigurationDefaults.INVOCATIONS_FILE);
    }

    public Path getConfigPath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, this.executionId, ConfigurationDefaults.CONFIGURATION_FILE);
    }

    public Path getRunFilePath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, this.executionId + ".run");
    }

    public Path getLatestRunFilePath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, ConfigurationDefaults.LATEST_RUN_ID);
    }

    public int getInvocationCount() {
        if (this.invoCount == null) {
            File failed = Paths.get(ConfigurationDefaults.NONDEX_DIR, executionId, ConfigurationDefaults.INVOCATIONS_FILE)
                    .toFile();

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
        return invoCount;
    }

    public Collection<String> getFailedTests() {
        if (failedTests == null) {
            this.failedTests = new HashSet<>();
            File failed = Paths.get(ConfigurationDefaults.NONDEX_DIR, executionId, ConfigurationDefaults.FAILURES_FILE)
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
        return Collections.unmodifiableCollection(failedTests);
    }

    private long numChoices() {
        assert (this.end >= this.start);
        return this.end - this.start;
    }

    public boolean hasLessChoicePoints(Configuration debConfig) {
        if (this.numChoices() < debConfig.numChoices()) {
            return true;
        }
        return false;
    }
}
