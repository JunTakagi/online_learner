package jtakagi.online.learner;

import jtakagi.online.instance.Instance;

public class PassiveAggressiveII extends Learner {
  public static final float DEFAULT_C = 0.1f;
  protected float C2;

  public PassiveAggressiveII(int size) {
    super(size);
    C2 = DEFAULT_C * 2.0f;
  }

  @Override
  public void setup(String[] args) {
    if (args.length < 1) {
      System.err.println("C is not setted. use default value C = " + DEFAULT_C);
      return;
    }
    try {
      C2 = Float.parseFloat(args[0]) * 2.0f;
    } catch (NumberFormatException e) {
      throw new RuntimeException("parse Error C:" + args[0]);
    }
  }

  /**
   * 更新式：
   * w_t+1 = w_t + loss/{||x||^2 + 1/(2C)} yx
   */
  public boolean update(Instance i) {
    float y = i.getLabel()? 1.0f:-1.0f;

    float loss = 1.0f - y * this.dot(i);
    if (loss > 0.0f) {
      Instance delta = new Instance(i);
      float factor = loss  / (i.sq2() + 1.0f / C2) * y;
      delta.scale(factor);
      this.add(delta);
      return true;
    }
    return false;
  }
}
