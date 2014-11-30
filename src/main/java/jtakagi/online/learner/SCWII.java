package jtakagi.online.learner;
import java.util.Arrays;

import jtakagi.online.instance.Instance;
import jtakagi.online.math.MatrixUtil;

// for debug
import java.util.Scanner;

/**
 * Exact Soft Confidence Weighted Linear Classifier - II
 * SCW-Iと更新式を比較すると、alphaの計算のみ変わっていて、その他は全て同じなので、
 * SCW-Iを継承してalphaの計算の部分のみ書き換える
 */
public class SCWII extends SCWI {
  public SCWII(int size) { super(size); }
  public SCWII(int size, String file) { super(size); }

  /**
   * alpha = max{0, (-2mn + phi mv + r) / 2(n^2 nv phi^2)}
   * r = phi sqrt(phi^2 m^2 v^2 + 4nv(n+v phi^2))
   * n = v + 1/2C
   */
  public float calcAlpha(float m, float v) {
    float n = v + 0.5f/C;
    float mvphi = m * v * phi;
    float phiSq = phi * phi;
    float r = phi * (float) Math.sqrt(mvphi * mvphi + 4 * n * v * ( n + v * phiSq ));
    float alpha = (-2 * m * n + phiSq * m * v + r) / (2 * (n * n + n * v * phiSq));
    if (alpha <= 0.0f) {
      return 0.0f;
    }
    return alpha;
  }
}
