[![Build Status](https://travis-ci.org/TestingResearchIllinois/NonDex.svg?branch=master)](https://travis-ci.org/TestingResearchIllinois/NonDex)
[![Issue Count](https://codeclimate.com/github/TestingResearchIllinois/NonDex/badges/issue_count.svg)](https://codeclimate.com/github/TestingResearchIllinois/NonDex)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4ef0b45fa77a4d58af5e23917c9bf5ae)](https://www.codacy.com/app/gyori/NonDex?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=TestingResearchIllinois/NonDex&amp;utm_campaign=Badge_Grade)

Prerequisites:
==============
    - OpenJdk 8.
    - Junit 4.7 and greater.
    - Surefire present in the POM.

Build:
======

    mvn install

Use (Maven):
============

After installing, add the plugin to your plugins section and the
dependency in your pom:

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
        <version>1.0.0</version>
      </plugin>
    </plugins>
  </build>

  <dependencies>
    ...
    <dependency>
      <groupId>edu.illinois</groupId>
      <artifactId>nondex-maven-plugin</artifactId>
      <version>1.0.0</version>
    </dependency>
  </dependencies>
</project>
```

To find if you have bad tests, run:

    mvn nondex:nondex

To debug, run:

    mvn nondex:debug

Use (Command-line):
===================

After installing, if your application uses the same Java version as you use to build NonDex, run:

    root=<path to NonDex root>
    instrumentedjar=${root}/nondex-instrumentation/resources/out.jar
    # Use the instrumented jar to run your application
    commonjar=${root}/nondex-common/target/nondex-common-1.0.0.jar
    java -Xbootclasspath/p:${instrumentedjar}:${commonjar} <application>

Optionally, in case your application needs a different Java version than the one you use to build NonDex, after installing, run:

    root=<path to NonDex root>
    instrumentingjar=${root}/nondex-instrumentation/target/nondex-instrumentation-1.0.0.jar
    instrumentedjar=${root}/<unique name of the output jar, such as out.jar>
    java -jar ${instrumentingjar} <path to rt.jar> ${instrumentedjar}
    # Use the instrumented jar to run your application
    commonjar=${root}/nondex-common/target/nondex-common-1.0.0.jar
    java -Xbootclasspath/p:${instrumentedjar}:${commonjar} <application>
