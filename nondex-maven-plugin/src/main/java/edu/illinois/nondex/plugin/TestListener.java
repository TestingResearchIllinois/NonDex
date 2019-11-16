package edu.illinois.nondex.plugin;

import edu.illinois.nondex.common.NonDex;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

@RunListener.ThreadSafe
public class TestListener extends RunListener{
    public void testRunStarted(Description description) throws java.lang.Exception
    {
        System.out.println("Number of tests to execute : " + description.testCount());
    }
    public void testStarted(Description description) throws java.lang.Exception
    {
        System.out.println("Updated Starting execution of test case: "+ description.getMethodName());
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
    }
}

