package edu.illinois.nondex.gradle.tasks;

import edu.illinois.nondex.gradle.internal.NonDexTestExecuter;
import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestExecuter;
import org.gradle.api.tasks.testing.Test;

import java.lang.reflect.Method;

public class NonDexTest extends AbstractNonDexTest {

    static final String NAME = "nondexTest";

    public static String getNAME() { return NAME; }

    public NonDexTest() {
        setDescription("Test with NonDex");
        setGroup("NonDex");
        NonDexTestExecuter nondexTestExecuter = createNondexTestExecuter();
        setNondexAsTestExecuter(nondexTestExecuter);
    }

    @Override
    public void executeTests() {
        setUpNondexTesting();
        super.executeTests();
    }

    private NonDexTestExecuter createNondexTestExecuter() {
        try {
            Method getExecuter = Test.class.getDeclaredMethod("createTestExecuter");
            getExecuter.setAccessible(true);
            TestExecuter<JvmTestExecutionSpec> delegate = (TestExecuter<JvmTestExecutionSpec>) getExecuter.invoke(this);
            return new NonDexTestExecuter(this, delegate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setNondexAsTestExecuter(NonDexTestExecuter nondexExecuter) {
        try {
            Method setTestExecuter = Test.class.getDeclaredMethod("setTestExecuter", TestExecuter.class);
            setTestExecuter.setAccessible(true);
            setTestExecuter.invoke(this, nondexExecuter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
