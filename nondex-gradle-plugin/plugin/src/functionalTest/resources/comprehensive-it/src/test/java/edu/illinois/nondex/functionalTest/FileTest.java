package edu.illinois.nondex.functionalTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class FileTest {

    private File myDir;

    @Before
    public void setUp() {
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

    /*@Test
    public void listRootsTest() {

    }*/
}
