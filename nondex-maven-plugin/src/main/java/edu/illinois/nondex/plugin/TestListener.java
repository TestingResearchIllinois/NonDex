package edu.illinois.nondex.plugin;

import edu.illinois.nondex.common.NonDex;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

@RunListener.ThreadSafe
public class TestListener extends RunListener{
    public void testRunStarted(Description description) throws java.lang.Exception
    {
    }
    public void testStarted(Description description) throws java.lang.Exception
    {
        String testName = description.getClassName() + "." + description.getMethodName();
        if(testName.contains("[") && testName.contains("]")) {
            testName = description.getClassName();
        }
        NonDex.getInstance().currTest = testName;
    }

    @Override
    public void testFinished(Description description) throws Exception {
        NonDex.getInstance().currTest = null;
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        NonDex.getInstance().currTest = null;

    }
}

