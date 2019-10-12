package edu.illinois.nondex.shuffling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Writter extends Thread {
    String msg;
    public Writter(String str){
        msg = str;
    }
    @Override
    public void run() {
        try {
            //Files.write(Paths.get("/Users/cnic/Desktop/research/artifact_id/log"), msg.getBytes());
            throw new IOException();
        } catch (IOException e) {

        }
    }
}