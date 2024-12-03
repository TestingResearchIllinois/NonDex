package edu.illinois.nondex.gradle.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MavenChecksumFetcher {

    /**
     * Fetches the SHA1 checksum of a JAR file from Maven Central repository.
     *
     * This method constructs the URL to the checksum file for a given artifact based on its
     * groupId, artifactId, and version, then makes an HTTP GET request to fetch the SHA1 checksum.
     * It returns the SHA1 checksum as a string.
     *
     * @param groupId     The group ID of the artifact (e.g., "edu.illinois")
     * @param artifactId  The artifact ID of the artifact (e.g., "nondex-common")
     * @param version     The version of the artifact (e.g., "2.2.1")
     * @return            The SHA1 checksum of the artifact as a string
     */
    public static String getSHA1(String groupId, String artifactId, String version) {
        try {
            String baseUrl = "https://repo1.maven.org/maven2/";
            String sha1Url = baseUrl + groupId.replace(".", "/") + "/" + artifactId + "/" + version + "/" 
                            + artifactId + "-" + version + ".jar.sha1";

            URL url = new URL(sha1Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return reader.readLine(); // Returns the SHA1 checksum
            }
        } catch (Exception e) {
            return null;
        }   
    }
    
}
