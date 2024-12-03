package edu.illinois.nondex.gradle.constants;

import edu.illinois.nondex.gradle.util.MavenChecksumFetcher;

public abstract class NonDexGradlePluginConstants {

    public static final String NONDEX_VERSION = "2.2.1";
    public static final String NONDEX_COMMON_SHA1 = MavenChecksumFetcher.getSHA1(
        "edu.illinois",
        "nondex-common",
        NONDEX_VERSION
    );
    
}
