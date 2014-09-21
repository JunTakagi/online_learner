package jtakagi.online.learner;
import java.util.Arrays;

import jtakagi.online.instance.Instance;

/**
 * Confidence Weighted Linear Classifier
 */
public class CWLC extends Learner {
  public static final float DEFAULT_PHI = 0.3f;

  protected float[] sigmas;
  protected float phi; // phi = phi^{-1}(nyu) 累積密度関数の逆関数。

  public CWLC(int size) {
    super(size);
    sigmas = new float[size];
    Arrays.fill(sigmas, 1.0f);
    phi = DEFAULT_PHI;
  }

  /**
   * file の対処方法は後で(Sigmaも吐き出す必要があるから)
   */
  public CWLC(int size, String file) {
    super(size);
    sigmas = new float[size];
    Arrays.fill(sigmas, 1.0f);
    phi = DEFAULT_PHI;
  }

  @Override
  public void setup(String[] args) throws NumberFormatException {
    if (args.length < 1) {
      System.err.println("Warning : phi is not setted, use default phi = " + DEFAULT_PHI);
    }
  }

  @Override
  public boolean update(Instance x) {
    float y = x.getLabel()? 1.0f:-1.0f;

    float M = y * this.dot(x);
    float V = xSigmax(x);
    float tmp = (1.0f + 2.0f * phi * M);
    float sqrtInner = tmp * tmp - 8.0f * phi * (M - phi * V);
    if (sqrtInner < 0.0f) {
      System.err.println("Cannot update weights because of imaginary number");
      return false;
    }
    float gamma = -tmp + (float) Math.sqrt(sqrtInner);
    gamma /= 4.0f * phi * V;
    if (gamma < 0.0f) return false; // 更新無し

    /* mu の更新 */
    Instance updateVector = new Instance(x);
    x.scale(gamma * y);
    multBySigma(x);
    this.add(x);

    /* Sigma の更新 */
    updateSigma(x, gamma);
    return true;
  }

  /**
   * x^{T} \Sigma x の計算
   */
  public float xSigmax(Instance x) {
    float xSx = 0.0f;
    int size = x.getSize();
    for (int i=0; i<size; ++i) {
      float xi = x.getWeight(i);
      xSx += xi * sigmas[x.getIndex(i)] * xi;
    }
    return xSx;
  }

  /**
   * Sigma x の計算
   */
  public void multBySigma(Instance x) {
    int size = x.getSize();
    for (int i=0; i<size; ++i) {
      float xi = x.getWeight(i);
      xi *= sigmas[x.getIndex(i)];
      x.setByIndex(i, xi);
    }
  }

  /**
   * Sigma の更新
   * Sigma^{-1} = Simga^{-1} + 2 * alpha * phi * diag(x)
   */
  public void updateSigma(Instance x, float gamma) {
    int size = x.getSize();
    for (int i=0; i<size; ++i) {
      float xi = x.getWeight(i);
      int index = x.getIndex(i);
      sigmas[index] = 1.0f / ( (1.0f / sigmas[index]) + 2.0f * gamma * phi * xi);
    }
  }
}
