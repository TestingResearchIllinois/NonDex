/*
The MIT License (MIT)
Copyright (c) 2015 University of Illinois

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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class Configuration {
    public final Mode mode;
    public final int seed;
    public final Pattern filter;

    public final String id;

    public final long start;
    public final long end;

    public final String testName;

    public Configuration(Mode mode, int seed, String filter, String executionId) {
        this(mode, seed, Pattern.compile(filter), 0, Long.MAX_VALUE, null, executionId);
    }

    protected Configuration(Mode mode, int seed, Pattern filter, long start, long end, String testName,
            String executionId) {
        this.mode = mode;
        this.seed = seed;
        this.filter = filter;
        this.start = start;
        this.end = end;
        this.testName = testName;
        this.id = executionId;

        this.createExecutionDirIfNeeded();
    }

    public String toArgLine() {
        return "-D" + ConfigurationDefaults.PROPERTY_DEFAULT_FILTER + "=" + "\'" + this.filter + "\'" + " -D"
                + ConfigurationDefaults.PROPERTY_DEFAULT_MODE + "=" + this.mode + " -D"
                + ConfigurationDefaults.PROPERTY_DEFAULT_SEED + "=" + this.seed + " -D"
                + ConfigurationDefaults.PROPERTY_DEFAULT_START + "=" + this.start + " -D"
                + ConfigurationDefaults.PROPERTY_DEFAULT_END + "=" + this.end + " -D"
                + ConfigurationDefaults.PROPERTY_DEFAULT_EXECUTION_ID + "=" + this.id
                + (this.testName == null ? "" : "-Dtest=" + this.testName);
    }

    public String toString() {
        return ConfigurationDefaults.PROPERTY_DEFAULT_FILTER + "=" + this.filter + "\n"
                + ConfigurationDefaults.PROPERTY_DEFAULT_MODE + "=" + this.mode + "\n"
                + ConfigurationDefaults.PROPERTY_DEFAULT_SEED + "=" + this.seed + "\n"
                + ConfigurationDefaults.PROPERTY_DEFAULT_START + "=" + this.start + "\n"
                + ConfigurationDefaults.PROPERTY_DEFAULT_END + "=" + this.end + "\n" + "test="
                + (this.testName == null ? "" : this.testName);
    }

    public static Configuration parseArgs() {
        String executionId = System.getProperty(ConfigurationDefaults.PROPERTY_DEFAULT_EXECUTION_ID,
                ConfigurationDefaults.NO_EXECUTION_ID);
        int seed = Integer.parseInt(System.getProperty(ConfigurationDefaults.PROPERTY_DEFAULT_SEED,
                ConfigurationDefaults.DEFAULT_SEED_STR));
        Mode nonDetKind = Mode.valueOf(System.getProperty(ConfigurationDefaults.PROPERTY_DEFAULT_MODE,
                ConfigurationDefaults.DEFAULT_MODE_STR));
        Pattern filter = Pattern.compile(System.getProperty(ConfigurationDefaults.PROPERTY_DEFAULT_FILTER,
                ConfigurationDefaults.DEFAULT_FILTER));
        long start = Long.parseLong(System.getProperty(ConfigurationDefaults.PROPERTY_DEFAULT_START,
                ConfigurationDefaults.DEFAULT_START_STR));
        long end = Long.parseLong(
                System.getProperty(ConfigurationDefaults.PROPERTY_DEFAULT_END, ConfigurationDefaults.DEFAULT_END_STR));
        String testName = System.getProperty("test", null);

        return new Configuration(nonDetKind, seed, filter, start, end, testName, executionId);
    }

    public void createExecutionDirIfNeeded() {
        Paths.get(ConfigurationDefaults.NONDEX_DIR, this.id).toFile().mkdirs();
    }

    public Path getFailuresPath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, this.id, ConfigurationDefaults.FAILURES_FILE);
    }

    public Path getInvocationsPath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, this.id, ConfigurationDefaults.INVOCATIONS_FILE);
    }

    public Path getConfigPath() {
        return Paths.get(ConfigurationDefaults.NONDEX_DIR, this.id, ConfigurationDefaults.CONFIGURATION_FILE);
    }
}
