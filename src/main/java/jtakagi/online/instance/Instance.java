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

  public float norm() {
    float norm = 0.0f;
    for (int i=0; i<size; ++i) {
      norm += weights[i] * weights[i];
    }
    return norm;
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
    for (int i=0; i<size; ++i) {
      System.out.println(indices[i] + ":" + weights[i]);
    }
  }
}

