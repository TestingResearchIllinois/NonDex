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

public class ConfigurationDefaults {

    // TODO(gyori): This should be autogenerated and not needed to be changed manually
    public static final String VERSION = "1.0.1-SNAPSHOT";

    public static final String PROPERTY_MODE = "nondex.mode";
    public static final String DEFAULT_MODE_STR = "FULL";
    public static final Mode DEFAULT_MODE = Mode.valueOf(ConfigurationDefaults.DEFAULT_MODE_STR);


    public static final String PROPERTY_SEED = "nondex.seed";
    public static final String DEFAULT_SEED_STR = "9928532";
    public static final int DEFAULT_SEED = new Integer(ConfigurationDefaults.DEFAULT_SEED_STR);

    public static final String DEFAULT_RERUN_STR = "false";
    public static final String PROPERTY_RERUN = "nondex.rerun";

    public static final String PROPERTY_NUM_RUNS = "nondex.runs";
    public static final String DEFAULT_NUM_RUNS_STR = "3";
    public static final int DEFAULT_NUM_RUNS = new Integer(ConfigurationDefaults.DEFAULT_NUM_RUNS_STR);

    public static final String PROPERTY_FILTER = "nondex.filter";
    public static final String DEFAULT_FILTER = ".*";

    public static final String PROPERTY_START = "nondex.start";
    public static final String DEFAULT_START_STR = "0";
    public static final long DEFAULT_START = new Long(ConfigurationDefaults.DEFAULT_START_STR);

    public static final String PROPERTY_END = "nondex.end";
    public static final String DEFAULT_END_STR = "9223372036854775807";
    public static final long DEFAULT_END = new Long(ConfigurationDefaults.DEFAULT_END_STR);

    public static final String PROPERTY_EXECUTION_ID = "nondex.execid";
    public static final String NO_EXECUTION_ID = "NoId";
    public static final String PROPERTY_DEFAULT_EXECUTION_ID = ConfigurationDefaults.NO_EXECUTION_ID;

    public static final String PROPERTY_RUN_ID = "nondex.runId";
    public static final String LATEST_RUN_ID = "LATEST";
    public static final String PROPERTY_DEFAULT_RUN_ID = ConfigurationDefaults.LATEST_RUN_ID;

    public static final String PROPERTY_NONDEX_DIR = "nondex.dir";
    public static final String DEFAULT_NONDEX_DIR = ".nondex";

    public static final String PROPERTY_NONDEX_JAR_DIR = "nondex.jar.dir";
    public static final String DEFAULT_NONDEX_JAR_DIR = ".nondex";

    public static final String INSTRUMENTATION_JAR = "nondex-instr.jar";
    public static final String FAILURES_FILE = "failures";
    public static final String INVOCATIONS_FILE = "invocations";
    public static final String DEBUG_FILE = "debug";
    public static final String CONFIGURATION_FILE = "config";

    public static final int SEED_FACTOR = 0xA1e4;

    public static final String PROPERTY_LOGGING_LEVEL = "nondex.logging";
    public static final String DEFAULT_LOGGING_LEVEL = "ALL";
}
