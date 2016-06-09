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

package edu.illinois.nondex.plugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.illinois.nondex.common.ConfigurationDefaults;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Removes NonDex plugin artifacts (i.e. the ".nondex" directories in each
 * module root) as well as the directory containig the NonDex jar.
 */
@Mojo(name = "clean", requiresDirectInvocation = true)
public class CleanMojo extends AbstractNondexMojo {

    public void execute() throws MojoExecutionException {
        Path nondexArtifactsPath = Paths.get(this.baseDir.getAbsolutePath(), ConfigurationDefaults.DEFAULT_NONDEX_DIR);
        Path nondexJarPath = Paths.get(this.baseDir.getAbsolutePath(), ConfigurationDefaults.DEFAULT_NONDEX_JAR_DIR);

        File artifactsDir = nondexArtifactsPath.toFile();
        File nondexJarDir = nondexJarPath.toFile();

        if (nondexJarDir.exists()) {
            delete(nondexJarDir);
        }

        if (artifactsDir.exists()) {
            delete(artifactsDir);
        }
    }

    public void delete(File file) {
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                delete(childFile);
            }
        }
        file.delete();
    }
}
