# NonDex

[![Build Status](https://travis-ci.org/TestingResearchIllinois/NonDex.svg?branch=master)](https://travis-ci.org/TestingResearchIllinois/NonDex)
[![Build status](https://ci.appveyor.com/api/projects/status/7cw58oph5346xvm0/branch/master?svg=true)](https://ci.appveyor.com/project/alexgyori/nondex/branch/master)
[![Issue Count](https://codeclimate.com/github/TestingResearchIllinois/NonDex/badges/issue_count.svg)](https://codeclimate.com/github/TestingResearchIllinois/NonDex)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4ef0b45fa77a4d58af5e23917c9bf5ae)](https://www.codacy.com/app/gyori/NonDex?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TestingResearchIllinois/NonDex&amp;utm_campaign=Badge_Grade)

NonDex is a tool for detecting and debugging wrong assumptions on under-determined Java APIs. An
example of such an assumption is when code assumes the order of iteration
through the entries in a `java.util.HashMap` is deterministic
, while the [specification](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html) for `java.util.HashMap` states that its
iteration order is not guaranteed to be in any particular order. Such
assumptions can hurt portability for an application when they are moved to
other environments with a different Java runtime. NonDex explores different behaviors of 
under-determined APIs and reports test failures under different explored behaviors; 
NonDex only explores behaviors that are allowed by the specification, so tests that fail or flake under NonDex instrumentation likely indicate false assumptions on a deterministic implementation of an under-determined Java API. NonDex helps expose such "implementation-dependent" flaky tests to the developers early, so they can fix the assumptions before they
become actual bugs in the future and/or even propagate to other projects.

Supported APIs:
===============
The list of supported APIs can be found [here](https://github.com/TestingResearchIllinois/NonDex/wiki/Supported-APIs)

Prerequisites:
==============
    - Java 8 ~ 17 (Oracle JDK, OpenJDK).
    - Maven 3.6+ and Surefire present in the POM.(for the NonDex Maven plugin).
    - Gradle 5.0+ (for the NonDex Gradle plugin).


Build (Maven):
======

    mvn install

Build (Gradle):
======

    cd nondex-gradle-plugin
    ./gradlew build

Use (Maven - Command-line):
============

To find if you have flaky tests under NonDex shuffling, run (use the ``-Dtest=...`` filter for individual tests):

    mvn edu.illinois:nondex-maven-plugin:2.1.7:nondex

To debug, run:

    mvn edu.illinois:nondex-maven-plugin:2.1.7:debug
    
The NonDex Maven plugin also offers additional options; to see them all, run:

    mvn edu.illinois:nondex-maven-plugin:2.1.7:help

 
Use (Maven - Add Plugin):
============ 
Add the NonDex plugin to the plugins section under the build section in your `pom.xml`:

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
        <version>2.1.7</version>
      </plugin>
    </plugins>
  </build>
</project>
```

To find if you have flaky tests, run:

    mvn nondex:nondex

To debug, run:

    mvn nondex:debug
    
The NonDex Maven plugin also offers additional options; to see them all, run:

    mvn nondex:help


Use (Gradle):
============

To use NonDex in Gradle (Groovy), add the following content into your `build.gradle`:

```groovy
plugins {
  id 'edu.illinois.nondex' version '2.1.7'
}
```
Apply it to subprojects (if any, optional):
```
subprojects {
  apply plugin: 'edu.illinois.nondex'
}
```

To use NonDex in Gradle (Kotlin), add the following content into your `build.gradle.kts`:
```kotlin
plugins {
  id("edu.illinois.nondex") version "2.1.7"
}
```
Apply it to subjects (if any, optional):
```
subprojects {
  apply(plugin = "edu.illinois.nondex")
}
```

Alternatively, if you are on Linux or MacOS, can use the existing script to set up NonDex in gradle build files automatically:
```
cd modify-gradle-build
./add-nondex.sh ${path to the root directory of the project to run NonDex in}
```

To find if you have flaky tests, run (use the ``--tests`` filter for individual tests):

    ./gradlew nondexTest

To debug, run:

    ./gradlew nondexDebug

To get the help information of NonDex Gradle plugin, run:

    ./gradlew nondexHelp


Use (Command-line):
===================

After installing, if your application uses the same Java version as you use to build NonDex, run:

    root=<path to NonDex root>
    instrumentedjar=${root}/nondex-instrumentation/resources/out.jar
    # Use the instrumented jar to run your application
    commonjar=${root}/nondex-common/target/nondex-common-2.1.7.jar
    java -Xbootclasspath/p:${instrumentedjar}:${commonjar} <application>

Optionally, in case your application needs a different Java version than the one you use to build NonDex, after installing, run:

    root=<path to NonDex root>
    instrumentingjar=${root}/nondex-instrumentation/target/nondex-instrumentation-2.1.7.jar
    instrumentedjar=${root}/<unique name of the output jar, such as out.jar>
    java -jar ${instrumentingjar} <path to rt.jar> ${instrumentedjar}
    # Use the instrumented jar to run your application
    commonjar=${root}/nondex-common/target/nondex-common-2.1.7.jar
    java -Xbootclasspath/p:${instrumentedjar}:${commonjar} <application>

Output:
=======

If there are flaky tests (passes in the run without NonDex shuffling but fails in one of the NonDex-shuffled runs), the output will report them under the section marked `"NonDex SUMMARY:"`.

The flaky tests are also logged in files called "failure" in the `.nondex/`
directory.  Each execution is identified by an execution ID (also reported in
the Maven output) and an execution that has a "failure" file in the `.nondex/ directory` with the same name as the execution ID.

Output (Debug):
===============

After running the debug task, the Maven output reports for each flaky test both the
command-line arguments to pass in to reproduce the failure and the path to the
file containing the debug results for the flaky test. These files are named
"debug", and they contain the name of the debugged test and the stack trace for
the single invocation point that when run through NonDex leads to the test
failing. If the test cannot be debugged to this single point, the debugger will report that the cause cannot be reproduced and may be
flaky due to other reasons.
