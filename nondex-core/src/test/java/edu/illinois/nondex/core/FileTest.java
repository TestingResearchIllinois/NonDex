package edu.illinois.nondex.core;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class FileTest {

    File myDir;
    File myFile;

    @Before
    public void setup() {
        myDir = new File(".");
    }

    @Test
    public void listTest() {
        assertThat(myDir.list(), not(equalTo(myDir.list())));
    }

    @Test
    public void listFilesTest() {
        assertThat(myDir.listFiles(), not(equalTo(myDir.listFiles())));
    }

    @Test
    public void listRootsTest() {

    }
}
