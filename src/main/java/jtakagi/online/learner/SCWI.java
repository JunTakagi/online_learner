package jtakagi.online.learner;
import java.util.Arrays;

import jtakagi.online.instance.Instance;
import jtakagi.online.math.MatrixUtil;

// for debug
import java.util.Scanner;

/**
 * Exact Soft Confidence Weighted Linear Classifier - I
 */
public class SCWI extends Learner {
  public static final float DEFAULT_PHI = 0.3f;
  public static final float DEFAULT_C = 0.01f;

  protected float[][] sigma;
  protected float phi; // phi = phi^{-1}(nyu) 累積密度関数の逆関数。
  protected float psi; // psi = 1+(phi^2)/2
  protected float zeta; // zeta = 1+phi^2
  protected float C;
  protected boolean verbose;

  // 更新計算用
  protected float[] tmpVec;
  protected float[][] tmpMat;

  public SCWI(int size) {
    super(size);
    sigma = new float[size][size];
    tmpMat = new float[size][size];
    for (int i=0; i<size; ++i) {
      Arrays.fill(sigma[i], 0.0f);;
      sigma[i][i] = 1.0f;
      Arrays.fill(tmpMat[i], 0.0f);;
    }
    this.phi = DEFAULT_PHI;
    float phiSq = phi*phi;
    this.psi = 1.0f + phiSq*0.5f;
    this.zeta = 1.0f + phiSq;
    this.C = DEFAULT_C;

    tmpVec = new float[size];
    Arrays.fill(tmpVec, 1.0f);
  }

  /**
   * file の対処方法は後で(Sigmaも吐き出す必要があるから)
   */
  public SCWI(int size, String file) {
    super(size);
    sigma = new float[size][size];
    tmpMat = new float[size][size];
    for (int i=0; i<size; ++i) {
      Arrays.fill(sigma[i], 0.0f);;
      sigma[i][i] = 1.0f;
      Arrays.fill(tmpMat[i], 0.0f);;
    }
    this.phi = DEFAULT_PHI;
    float phiSq = phi*phi;
    this.psi = 1.0f + phiSq*0.5f;
    this.zeta = 1.0f + phiSq;
    this.C = DEFAULT_C;

    tmpVec = new float[size];
    Arrays.fill(tmpVec, 1.0f);
  }

  @Override
  public void setup(String[] args) throws NumberFormatException {
    super.setup(args);
    if (args.length < 1) {
      System.err.println("Warning : phi is not setted, use default phi = " + CWLC.DEFAULT_PHI);
      System.err.println("Warning : C is not setted, use default phi = " + DEFAULT_C);
      return;
    }
    this.phi = Float.parseFloat(args[0]);
    float phiSq = phi*phi;
    this.psi = 1.0f + phiSq*0.5f;
    this.zeta = 1.0f + phiSq;
    if (args.length < 2) {
      System.err.println("Warning : C is not setted, use default phi = " + DEFAULT_C);
      return;
    }
    this.C = Float.parseFloat(args[1]);
    if (args.length >= 3) {
      this.verbose = Boolean.parseBoolean(args[2]);
    }
    if (verbose) {
      dump();
    }
  }

  @Override
  public boolean update(Instance x) {
    float y = x.getLabel()? 1.0f:-1.0f;

    float M = y * this.dot(x);

    // V = x \Sigma xの計算
    MatrixUtil.Mv(sigma, x, tmpVec);
    float V = MatrixUtil.vecDot(x, tmpVec);

    float alpha = calcAlpha(M, V);
    if (alpha <= 0.0f) return false;

    float u = calcU(alpha, V);
    float beta = calcBeta(alpha, V, u);

    /** mu の更新
     * mu += \alpha * y * Sigma * x
     */
    float factor = alpha * y;
    MatrixUtil.Mv(sigma, x, tmpVec);
    for (int i=0; i<weights.length; ++i) {
      weights[i] += factor * tmpVec[i];
    }

    /** Sigma の更新
     * Sigma -= \beta \Sigma x^{T} x \Sigma
     * Sigmaは対象行列なので、 \Sigma x^{T} の外積で計算できる。
     * muの更新の際に tmpVec が \Sigma x になっているので使い回せる
     */
    MatrixUtil.vecCross(tmpVec, tmpVec, tmpMat);
    for (int i=0; i<sigma.length; ++i) {
      for (int j=0; j<sigma[0].length; ++j) {
        sigma[i][j] -= beta * tmpMat[i][j];
      }
    }

    return true;
  }

  /**
   * alpha = min{C, max{0, 1/(v + zeta) (- m * psi + sqrt(m^2 phi^4/4 + v phi^2 zeta))
   */
  public float calcAlpha(float m, float v) {
    float phiSq = phi * phi;
    float phi4 = phiSq * phiSq;
    float alpha = (1.0f / (v + zeta)) * (- m * psi + ((float)Math.sqrt(m * m * phi4 + v * phiSq * zeta)));
    if (alpha < 0.0f) {
      return 0.0f;
    } else if (alpha > C) {
      return C;
    }
    return alpha;
  }

  /**
   * u = 1/4 ( - alpha * v * phi + sqrt(alpha^2 * v^2 * phi^2 + 4v))^2
   */
  public float calcU(float alpha, float v) {
    float avf = alpha * v * phi;
    float sqrtU = - avf + ((float)Math.sqrt(avf * avf + 4 * v));
    float u = 0.25f * sqrtU * sqrtU;
    return u;
  }

  /**
   * beta = alpha * phi / (sqrt(u) + v * alpha * phi)
   */
  public float calcBeta(float alpha, float v, float u) {
    float beta = alpha * phi / (((float)Math.sqrt(u)) + alpha * v * u);
    return beta;
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
    for (int i=0; i<sigma.length; ++i) {
      System.out.print(i + ":" + sigma[i] + " ");
    }
    System.out.println();
    System.out.println("===========================");
  }
}
