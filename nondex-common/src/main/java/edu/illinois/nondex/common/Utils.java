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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import javax.xml.bind.DatatypeConverter;

public class Utils {

    public static Throwable linkException(Throwable nestedThrowable, Throwable pastSupressedException) {
        if (pastSupressedException == null) {
            return nestedThrowable;
        }
        if (nestedThrowable == null) {
            return pastSupressedException;
        }
        pastSupressedException.addSuppressed(nestedThrowable);
        return pastSupressedException;
    }

    public static Properties openPropertiesFrom(Path path) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(path.toFile()));
        } catch (IOException ioe) {
            Logger.getGlobal().log(Level.CONFIG, "Cannot open properties file!", ioe);
        }
        return props;
    }

    public static int computeIthSeed(int ithSeed, boolean rerun, int seed) {
        if (rerun) {
            return seed;
        } else {
            return seed + ithSeed * ConfigurationDefaults.SEED_FACTOR;
        }
    }

    public static String getFreshExecutionId() {
        try {
            // TODO(gyori): Fix to check that the id was not used before in the
            // .nondex (?)
            String id = DatatypeConverter.printBase64Binary(
                    MessageDigest.getInstance("SHA-256").digest(Long.toString(System.currentTimeMillis()).getBytes()));
            id = id.replace("/", "");
            id = id.replace("\\", "");
            return id;
        } catch (NoSuchAlgorithmException nsae) {
            return "No_ID";
        }
    }

    // TODO: How about JDK older than JDK8
    public static boolean checkJDK8() {
        return System.getProperty("java.version").startsWith("1.8");
    }

    public static Path getRtJarLocation() {
        String javaHome = System.getProperty("java.home");
        if (javaHome == null) {
            Logger.getGlobal().log(Level.SEVERE, "JAVA_HOME is not set!");
            throw new IllegalStateException("JAVA_HOME is not set!");
        }

        // TODO: NonDex is implemented with JDK8 (Run with JDK8+ will fail)
        Path pathToRt = Paths.get(javaHome, "jre", "lib", "rt.jar");
        Logger.getGlobal().log(Level.FINE, pathToRt.toString());
        if (Files.exists(pathToRt)) {
            return pathToRt;
        }

        pathToRt = Paths.get(javaHome, "lib", "rt.jar");
        Logger.getGlobal().log(Level.FINE, pathToRt.toString());
        if (Files.exists(pathToRt)) {
            return pathToRt;
        }

        return null;
//        FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
//        Path rtClassPath = fs.getPath("modules/java.base");
//        Logger.getGlobal().log(Level.WARNING, rtClassPath.toString());
        /* Test jrt module
        try {
            InputStream is = Files.newInputStream(fs.getPath("modules/java.base",
                    "java/lang/Object.class"));
            Scanner scanner = new Scanner(is, "UTF-8");
            String text = scanner.useDelimiter("\\A").next();
            Logger.getGlobal().log(Level.WARNING, text);
            scanner.close();
        } catch (Exception e) {
            Logger.getGlobal().log(Level.WARNING, e.toString());
        }
        */

        // Indicate using JDK8+
//        return Paths.get(ConfigurationDefaults.JDK8_PLUS_PATH);
    }
}
