package jtakagi.online.instance;

import java.util.Arrays;

public class Instance {
  protected float[] weights;
  protected int[] indices;
  protected int size;
  protected boolean label;

  public Instance (Instance i) {
    this.size = i.size;
    this.weights = Arrays.copyOf(i.weights, size);
    this.indices = Arrays.copyOf(i.indices, size);
    this.label = i.label;
  }

  public Instance (float[] weights, int[] indices, boolean label) {
    this.set(weights, indices, label);
  }


  public void set(float[] weights, int[] indices, boolean label) {
    if (weights.length != indices.length) {
      throw new RuntimeException("Instance creation error : weight length " + weights.length + ", indices.length " + indices.length);
    }
    this.size = weights.length;
    this.weights = Arrays.copyOf(weights, size);
    this.indices = Arrays.copyOf(indices, size);
    this.label = label;
  }

  public boolean setByIndex(int index, float weight) {
    if (index > size) {
      this.weights[index] = weight;
      return true;
    }
    return false;
  }

  /**
   * 2norm$B$N(B2$B>r$r7W;;$9$k(B
   */
  public float sq2() {
    float sq2 = 0.0f;
    for (int i=0; i<size; ++i) {
      sq2 += weights[i] * weights[i];
    }
    return sq2;
  }
  public float norm() {
    return (float)Math.sqrt(this.sq2());
  }

  public void scale(float factor) {
    for (int i=0; i<size; ++i) {
      weights[i] *= factor;
    }
  }

  // getter method
  public float getWeight(int i) {
    return this.weights[i];
  }
  public float getWeightByIndex(int i) {
    int arrayInd = Arrays.binarySearch(this.indices, i);
    if (arrayInd < 0) return 0;
    return this.weights[arrayInd];
  }
  public int getIndex(int i) {
    return this.indices[i];
  }
  public boolean getLabel() {
    return this.label;
  }
  public int getSize() {
    return size;
  }

  // debug
  public void dump() {
    System.out.print(label);
    for (int i=0; i<size; ++i) {
      System.out.print(" " + indices[i] + ":" + weights[i]);
    }
    System.out.println();
  }
}

