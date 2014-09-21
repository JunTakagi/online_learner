package jtakagi.online.learner;

import java.lang.reflect.Constructor;

public class LearnerFactory {
  @SuppressWarnings("unchecked")
  public static Learner get(String type) throws Exception {
    ClassLoader loader = ClassLoader.getSystemClassLoader();
    Class clazz = loader.loadClass(type);
    Constructor constructor = clazz.getConstructor(int.class);
    return (Learner) constructor.newInstance(130);
  }
}
