package edu.illinois.nondex.gradle.internal;

import org.gradle.api.internal.tasks.testing.TestCompleteEvent;
import org.gradle.api.internal.tasks.testing.TestDescriptorInternal;
import org.gradle.api.internal.tasks.testing.TestResultProcessor;
import org.gradle.api.internal.tasks.testing.TestStartEvent;
import org.gradle.api.tasks.testing.TestOutputEvent;
import org.gradle.api.tasks.testing.TestFailure;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class NonDexTestProcessor implements TestResultProcessor {

    private final TestResultProcessor delegate;
    private Set<String> failingTests = new LinkedHashSet<>();
    private final Map<Object, TestDescriptorInternal> activeDescriptorsById = new HashMap<>();
    private Object rootTestDescriptorId;
    private boolean lastRun;
    private Method failureMethod;

    NonDexTestProcessor(TestResultProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void started(TestDescriptorInternal descriptor, TestStartEvent testStartEvent) {
        if (rootTestDescriptorId == null) {
            rootTestDescriptorId = descriptor.getId();
            activeDescriptorsById.put(descriptor.getId(), descriptor);
            delegate.started(descriptor, testStartEvent);
        } else if (!descriptor.getId().equals(rootTestDescriptorId)) {
            activeDescriptorsById.put(descriptor.getId(), descriptor);
            delegate.started(descriptor, testStartEvent);
        }
    }

    @Override
    public void completed(Object testId, TestCompleteEvent testCompleteEvent) {
        if (testId.equals(rootTestDescriptorId)) {
            if (!lastRun) {
                return;
            }
        } else {
            activeDescriptorsById.remove(testId);
        }
        delegate.completed(testId, testCompleteEvent);
    }

    @Override
    public void output(Object testId, TestOutputEvent testOutputEvent) {
        delegate.output(testId, testOutputEvent);
    }

    @Override
    public void failure(Object testId, TestFailure result) {
        failure(testId);
        delegate.failure(testId, result);
    }

    @SuppressWarnings("unused")
    public void failure(Object testId, Throwable throwable) {
        // Gradle 7.6 changed the method signature from failure(Object, Throwable) to failure(Object, TestFailure).
        // To maintain compatibility with older versions, the original method needs to exist and needs to call failure()
        // on the delegate via reflection.
        failure(testId);
        try {
            Method failureMethod = lookupFailureMethod();
            failureMethod.invoke(delegate, testId, throwable);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void reset(boolean lastRun) {
        if (this.lastRun) {
            throw new IllegalStateException("processor has completed");
        }
        this.lastRun = lastRun;
        this.activeDescriptorsById.clear();
        this.failingTests = new LinkedHashSet<>();
    }

    public Set<String> getFailingTests() {
        return this.failingTests;
    }

    private Method lookupFailureMethod() throws ReflectiveOperationException {
        if (failureMethod == null) {
            failureMethod = delegate.getClass().getMethod("failure", Object.class, Throwable.class);
        }
        return failureMethod;
    }

    private void failure(Object testId) {
        final TestDescriptorInternal descriptor = activeDescriptorsById.get(testId);
        if (descriptor != null) {
            String className = descriptor.getClassName();
            if (className != null) {
                String name = descriptor.getName();
                failingTests.add(className + "." + name);
            }
        }
    }
}
