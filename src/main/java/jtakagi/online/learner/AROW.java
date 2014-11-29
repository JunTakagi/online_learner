package jtakagi.online.learner;
import java.util.Arrays;

import jtakagi.online.instance.Instance;
import jtakagi.online.math.MatrixUtil;

// for debug
import java.util.Scanner;
import java.util.Arrays;

/**
 * AROW - Adaptive Regularization of Weight Vectors
 */
public class AROW extends Learner {
  public static final float DEFAULT_LAMBDA = 0.3f;
  public static int count = 1; //debug counter

  protected float[][] sigmas;
  protected float lambda; // phi = phi^{-1}(nyu) 累積密度関数の逆関数。

  // for calculation
  protected float[] tmpVec; // vertical
  protected float[][] tmpMat; // horizontal

  protected boolean verbose;

  public AROW(int size) {
    super(size);
    sigmas = new float[size][size];
    for (int i=0; i<size; ++i) {
      Arrays.fill(sigmas[i], 0.0f);
      sigmas[i][i] = 1.0f;
    }
    this.lambda = DEFAULT_LAMBDA;
    this.verbose = false;

    this.tmpVec = new float[size];
    this.tmpMat = new float[size][size];
    Arrays.fill(tmpVec, 0.0f);
  }

  /**
   * file の対処方法は後で(Sigmaも吐き出す必要があるから)
   */
  public AROW(int size, String file) {
    super(size);
    sigmas = new float[size][size];
    for (int i=0; i<size; ++i) {
      Arrays.fill(sigmas[i], 0.0f);
      sigmas[i][i] = 1.0f;
    }
    this.lambda = DEFAULT_LAMBDA;

    this.tmpVec = new float[size];
    this.tmpMat = new float[size][size];
    Arrays.fill(tmpVec, 0.0f);
  }

  @Override
  public void setup(String[] args) throws NumberFormatException {
    if (args.length < 1) {
      System.err.println("Warning : lambda is not setted, use default phi = " + DEFAULT_LAMBDA);
      return;
    }
    this.lambda = Float.parseFloat(args[0]);
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

    float loss = 1.0f - y * this.dot(x);
    if (loss <= 0) return false; //  損失が無いときは更新しない

    float beta = calcBeta(x);
    float alpha = loss * beta; // loss must larger than 0 here
    if (this.verbose) {
      System.out.println("beta: " + beta);
      System.out.println("alpha: " + alpha);
    }
    updateSigma(x, beta);
    updateMu(x, y, alpha);

    return true;
  }

  /**
   * \beta = 1.0 / (\lambda + x^{T} \Sigma x) の計算
   */
  public float calcBeta(Instance x) {
    MatrixUtil.vM(x, this.sigmas, tmpVec);
    float xSx = MatrixUtil.vecDot(x, tmpVec);
    return 1.0f / ( this.lambda + xSx );
  }

  /**
   * Sigmaの更新
   * \Sigma_new = \Sigma - \beta \Sigma x x^T \Sigma の計算
   * Sigmaは常に対象行列になるので、最後の項は(\Sigma x)の Crossをとるだけで足りる。
   */
  public void updateSigma(Instance x, float beta) {
    MatrixUtil.Mv(this.sigmas, x, tmpVec);
    MatrixUtil.vecCross(tmpVec, tmpVec, tmpMat);

    for (int i=0; i<this.sigmas.length; ++i) {
      for (int j=0; j<this.sigmas[0].length; ++j) {
        this.sigmas[i][j] -= beta * tmpMat[i][j];
      }
    }
  }

  /**
   * Mu の更新
   * \mu_new = \mu + alpha * \Sigma * yx
   */
  public void updateMu(Instance x, float y, float alpha) {
    float factor = y * alpha;
    MatrixUtil.Mv(this.sigmas, x, tmpVec);
    for (int i=0; i<this.weights.length; ++i) {
      this.weights[i] += factor * tmpVec[i];
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
      for (int j=0; j<sigmas[0].length; ++j) {
        System.out.print(i + ":" + sigmas[i][j] + " ");
      }
    }
    System.out.println();
    System.out.println("===========================");
  }
}
