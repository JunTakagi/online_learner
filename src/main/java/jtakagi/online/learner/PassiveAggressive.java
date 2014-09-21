package jtakagi.online.learner;

import jtakagi.online.instance.Instance;

public class PassiveAggressive extends Learner {
  public PassiveAggressive(int size) {
    super(size);
  }

  /**
   * 今はモック
   */
  public boolean update(Instance i) {
    float y = i.getLabel()? 1.0f:-1.0f;

    float loss = 1.0f - y * this.dot(i);
    if (loss > 0.0f) {
      Instance delta = new Instance(i);
      float factor = (loss * y) / i.norm();
      delta.scale(factor);
      this.add(delta);
      return true;
    }
    return false;
  }
}
