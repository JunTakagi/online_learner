package jtakagi.online.learner;

import jtakagi.online.instance.Instance;

public class PassiveAggressiveI extends Learner {
  public static final float DEFAULT_C = 0.1f;
  protected float C;

  public PassiveAggressiveI(int size) {
    super(size);
    C = DEFAULT_C;
  }

  @Override
  public void setup(String[] args) {
    if (args.length < 1) {
      System.err.println("C is not setted. use default value C = " + DEFAULT_C);
      return;
    }
    try {
      C = Float.parseFloat(args[0]);
    } catch (NumberFormatException e) {
      throw new RuntimeException("parse Error C:" + args[0]);
    }
  }

  /**
   * 更新式：
   * w_t+1 = w_t + min{C, loss/||x||^2} yx
   */
  public boolean update(Instance i) {
    float y = i.getLabel()? 1.0f:-1.0f;

    float loss = 1.0f - y * this.dot(i);
    if (loss > 0.0f) {
      Instance delta = new Instance(i);
      float factor = loss  / i.sq2();
      if (factor > C) factor = C;
      factor *= y;
      delta.scale(factor);
      this.add(delta);
      return true;
    }
    return false;
  }
}
