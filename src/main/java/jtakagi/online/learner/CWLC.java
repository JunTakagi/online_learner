package jtakagi.online.learner;
import java.util.Arrays;

import jtakagi.online.instance.Instance;

// for debug
import java.util.Scanner;

/**
 * Confidence Weighted Linear Classifier
 */
public class CWLC extends Learner {
  public static final float DEFAULT_PHI = 0.3f;
  public static int count = 1; //debug counter

  protected float[] sigmas;
  protected float phi; // phi = phi^{-1}(nyu) 累積密度関数の逆関数。

  protected boolean verbose;

  public CWLC(int size) {
    super(size);
    sigmas = new float[size];
    Arrays.fill(sigmas, 1.0f);
    this.phi = DEFAULT_PHI;
    this.verbose = false;
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
    this.phi = Float.parseFloat(args[0]);
    if (args.length >= 2) {
      this.verbose = Boolean.parseBoolean(args[1]);
    }
    if (verbose) {
      dump();
    }
  }

  @Override
  public boolean update(Instance x) {
    float y = x.getLabel()? 1.0f:-1.0f;

    /*
     * 大きく間違えると、Mの値が大きくなり、それにつられてgammaの値も大きくなる。
     * その際、更新幅が大きすぎるために、パラメータが大きくなり、次の更新も大きくなり・・・
     * という感じで Inf まで振れてしまうことが分かったので、
     * gammaが大きくなりすぎないように頭を抑えるヒューリスティックを入れた
     */
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

    if (verbose) {
      x.dump();
      System.out.println("score : " + score(x));
      System.out.println("M :" + M + " V: " + V + " gamma: " + gamma);
      System.out.println("tmp = " + tmp);
      System.out.println("sqrtInner = " + sqrtInner);
      System.out.println("denom = " +  4.0f * phi * V);
      if (gamma < 0.0f) {

        System.out.println("update is not needed");
        Scanner scan = new Scanner(System.in); // debugのストップ用
        scan.next();
      }
    }

    if (gamma <= 0.0f) return false; // 更新無し
    else if(gamma >= 1.0f) gamma = 1.0f; // gammaが大きくなりすぎるのを防ぐ


    /* mu の更新 */
    Instance updateVector = new Instance(x);
    updateVector.scale(gamma * y);
    multBySigma(updateVector);
    this.add(updateVector);

    /* Sigma の更新 */
    updateSigma(x, gamma);
    if (verbose) {
      updateVector.dump(); // debug
      this.dump();
      Scanner scanner = new Scanner(System.in); // debugのストップ用
      scanner.next();
    }

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

  @Override
  public void dump() {
    System.out.println("========== WEIGHT ==========");
    for (int i=0; i<weights.length; ++i) {
      if (weights[i] != 0.0f) {
        System.out.print(i + ":" + weights[i] + " ");
      }
    }
    System.out.println();
    System.out.println("========== SIGMA ==========");
    for (int i=0; i<sigmas.length; ++i) {
      System.out.print(i + ":" + sigmas[i] + " ");
    }
    System.out.println();
    System.out.println("===========================");
  }
}
