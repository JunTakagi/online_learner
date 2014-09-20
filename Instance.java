import java.util.Arrays;

public class Instance {
  float[] weights;
  int[] indices;
  int size;
  boolean label;

  public Instance (Instance i) {
    this.size = i.size;
    this.weights = Arrays.copyOf(i.weights, size);
    this.indices = Arrays.copyOf(i.indices, size);
    this.label = i.label;
  }

  public Instance (float[] weights, int[] indices, boolean label) {
    if (weights.length != indices.length) {
      throw new RuntimeException("Instance creation error : weight length " + weights.length + ", indices.length " + indices.length);
    }
    this.size = weights.length;
    this.weights = Arrays.copyOf(weights, size);
    this.indices = Arrays.copyOf(indices, size);
    this.label = label;
  }
}

