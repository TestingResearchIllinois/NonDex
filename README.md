[![Build Status](https://travis-ci.org/TestingResearchIllinois/NonDex.svg?branch=master)](https://travis-ci.org/TestingResearchIllinois/NonDex)
[![Issue Count](https://codeclimate.com/github/TestingResearchIllinois/NonDex/badges/issue_count.svg)](https://codeclimate.com/github/TestingResearchIllinois/NonDex)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4ef0b45fa77a4d58af5e23917c9bf5ae)](https://www.codacy.com/app/gyori/NonDex?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TestingResearchIllinois/NonDex&amp;utm_campaign=Badge_Grade)

NonDex is a tool for detecting and debugging wrong assumptions on under-determined Java APIs. An
example of such an assumption is when code assumes the order of iterating
through the entries in a java.util.HashMap is in a specific, deterministic
order, but the specification for java.util.HashMap is under-determined and states that this
iteration order is not guaranteed to be in any particular order. Such
assumptions can hurt portability for an application when they are moved to
other environments with a different Java runtime. NonDex explores different behaviors of 
under-determined APIs and reports test failures under different explored behaviors; 
NonDex only explores behaviors that are allowed by the specification and any test failure indicates an assumption on an under-determined Java API. NonDex helps expose such brittle
assumptions to the developers early, so they can fix the assumption before it
becomes a problem far in the future and more difficult to fix.

Supported APIs:
===============
The list of supported APIs can be found [here](https://github.com/TestingResearchIllinois/NonDex/wiki/Supported-APIs)

Prerequisites:
==============
    - Java 8 (Oracle JDK, OpenJDK).
    - Surefire present in the POM.

Build:
======

    mvn install

Use (Maven):
============

To use NonDex, add the plugin to the plugins section under the build section in your pom:

```xml
<project>
  ...
  <build>
    ...
    <plugins>
      ...
      <plugin>
        <groupId>edu.illinois</groupId>
        <artifactId>nondex-maven-plugin</artifactId>
        <version>1.1.0</version>
      </plugin>
    </plugins>
  </build>
</project>
```

To find if you have flaky tests, run:

    mvn nondex:nondex

To debug, run:

    mvn nondex:debug

Use (Command-line):
===================

After installing, if your application uses the same Java version as you use to build NonDex, run:

    root=<path to NonDex root>
    instrumentedjar=${root}/nondex-instrumentation/resources/out.jar
    # Use the instrumented jar to run your application
    commonjar=${root}/nondex-common/target/nondex-common-1.1.0.jar
    java -Xbootclasspath/p:${instrumentedjar}:${commonjar} <application>

Optionally, in case your application needs a different Java version than the one you use to build NonDex, after installing, run:

    root=<path to NonDex root>
    instrumentingjar=${root}/nondex-instrumentation/target/nondex-instrumentation-1.1.0.jar
    instrumentedjar=${root}/<unique name of the output jar, such as out.jar>
    java -jar ${instrumentingjar} <path to rt.jar> ${instrumentedjar}
    # Use the instrumented jar to run your application
    commonjar=${root}/nondex-common/target/nondex-common-1.1.0.jar
    java -Xbootclasspath/p:${instrumentedjar}:${commonjar} <application>

Output:
=======

If there are flaky tests, the output will report them under the section marked "NonDex SUMMARY:"

The flaky tests are also logged in files called "failure" in the .nondex/
directory.  Each execution is identified by an execution ID (also reported in
the Maven output), and an execution that has a "failure" file will have that
"failure" file in a directory in the .nondex/ directory with the same name as
the execution ID.

Output (Debug):
===============

After running debugging, the Maven output reports for each flaky test both the
command-line arguments to pass in to reproduce the failure and the path to the
file containing the debug results for the flaky test. These files are named
"debug", and they contain the name of the debugged test and the stack trace for
the single invocation point that when run through NonDex leads to the test
failing. If the test cannot be debugged to this single point, the Maven output
when indicate it by reporting that the cause cannot be reproduced and may be
flaky due to other reasons.
