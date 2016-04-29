package edu.illinois.nondex.core;

import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;

import org.junit.Test;

public class ClassTest {

    @Test
    public void smokeTest() {
      Class<HashMap> clazz = HashMap.class;
      assertNotEquals(clazz.getClasses(), clazz.getClasses());  
      assertNotEquals(clazz.getFields(), clazz.getFields());
      assertNotEquals(clazz.getDeclaredFields(), clazz.getDeclaredFields());
      assertNotEquals(clazz.getConstructors(), clazz.getConstructors());
      assertNotEquals(clazz.getDeclaredConstructors(), clazz.getDeclaredConstructors());
      assertNotEquals(clazz.getMethods(), clazz.getMethods());
      assertNotEquals(clazz.getDeclaredMethods(), clazz.getDeclaredMethods());
      // This class has no annotations, likely
      //assertNotEquals(clazz.getAnnotations(), clazz.getAnnotations());
      //assertNotEquals(clazz.getDeclaredAnnotations(), clazz.getDeclaredAnnotations());
    }
}
