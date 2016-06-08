package edu.illinois.nondex.instr;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.ClassVisitor;

public class CVFactory {
    public static ClassVisitor construct(ClassVisitor cv, String clzToInstrument) throws NoSuchAlgorithmException {
        if (clzToInstrument.equals(Instrumenter.concurrentHashMapName)) {
            return new ConcurrentHashMapShufflingAdder(cv);
        }
        else if (clzToInstrument.equals(Instrumenter.hashMapName)) {
            return new HashMapShufflingAdder(cv);
        }
        else if (clzToInstrument.equals(Instrumenter.methodName)) {
            return new MethodShufflingAdder(cv);
        } else {
            Logger.getGlobal().log(Level.CONFIG, "Trying to construct CV for " + clzToInstrument);
            throw new NoSuchAlgorithmException();
        }

    }
}
