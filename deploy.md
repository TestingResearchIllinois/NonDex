## Publish to Maven Central

To deploy for the first time, run the following

```shell
export GPG_TTY=$(tty)
gpg --gen-key
gpg2 --list-keys
gpg --keyserver hkp://185.125.188.26 --send-keys XXXXXXXX # key is from --list-keys

mvn versions:set -DnewVersion={new_version}
```
(You need not run the commands that start with `gpg` if you already generated a key before. You would need to run `export GPG...` and `mvn version...` though)

Make the following changes to the pom.xml

```shell
diff --git a/pom.xml b/pom.xml
index 0cf2fe9..56ccb2e 100644
--- a/pom.xml
+++ b/pom.xml
@@ -110,6 +110,10 @@
         <version>1.6</version>
         <executions>
           <execution>
+            <configuration>
+              <executable>gpg2</executable>
+              <passphrase>XXXXXXXX</passphrase> <!-- passphrase is set to public key outputted from gpg2 list-key -->
+            </configuration>
             <id>sign-artifacts</id>
             <phase>deploy</phase>
             <goals>
```


Modify the `~/.m2/settings.xml` on your machine:

```shell
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
  https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <localRepository/>
  <interactiveMode/>
  <offline/>
  <pluginGroups>
    <pluginGroup>org.openclover</pluginGroup>
  </pluginGroups>
  <mirrors/>
  <proxies/>
  <profiles/>
  <activeProfiles/>
  <servers>
    <server>
      <id>central</id>
      <username>xxx</username>
      <password>xxxxx</password>
    </server>
  </servers>
</settings>
```
Run `mvn clean deploy` to deploy. Then go to https://central.sonatype.com/publishing, find NonDex in the "Deployment" side bar (on the left), select it, and click "Publish".   Running `gpgconf --kill gpg-agent` between attempts to deploy may help if gpg was interrupted.

Last deploy was done on asedl in `~/iDFlakies-deploy`.


## Publish to Gradle Plugin Portal

First, update the version in the Gradle build file - the following is the example that bumps from v2.2.1 to v2.2.5:
```shell
diff --git a/nondex-gradle-plugin/plugin/build.gradle b/nondex-gradle-plugin/plugin/build.gradle
index 464d8fe..3846757 100644
--- a/nondex-gradle-plugin/plugin/build.gradle
+++ b/nondex-gradle-plugin/plugin/build.gradle
@@ -5,7 +5,7 @@ plugins {
 }
 
 group = "edu.illinois"
-version = "2.2.1"
+version = "2.2.5"
 
 repositories {
     mavenCentral()
```

Then modify `~/.gradle/gradle.properties` to add your API keys:
```shell
org.gradle.vfs.watch=true
gradle.publish.key=xxx
gradle.publish.secret=xxxxx
```

Finally, publish the NonDex Gradle plugin:
```shell
cd nondex-gradle-plugin
./gradlew publishPlugins
```
