package jtakagi.online.math;

import java.util.Arrays;

import jtakagi.online.instance.Instance;

/**
 * 最適化されていないとりあえずの計算用
 * ライブラリで置き換えたい
 */
public class MatrixUtil {

  /**
   * 行列コピー
   */
  public static boolean MatrixCopy(float[][] source, float[][] target) {
    if (source.length == 0 || source.length != target.length) {
      return false;
    }
    if (source[0].length == 0 || source[0].length != target[0].length) {
      return false;
    }
    for (int i = 0; i<source.length; ++i) {
      for (int j = 0; j<source[0].length; ++j) {
        target[i][j] = source[i][j];
      }
    }
    return true;
  }

  /**
   * 行列xベクトル
   * 行列サイズ [i][j], ベクトルサイズ [j](縦ベクトル), 結果サイズ[i](縦ベクトル)
   */
  public static boolean Mv(float[][] mat, float[] vec, float[] target) {
    if (mat.length == 0 || mat[0].length == 0
        || mat[0].length != vec.length || mat.length != target.length) {
      return false;
    }
    for (int i=0; i < mat.length; ++i) {
      float sum = 0.0f;
      for (int j=0; j < mat[0].length; ++j) {
        sum += mat[i][j] * vec[j];
      }
      target[i] = sum;
    }
    return true;
  }

  /**
   * 行列xベクトル スパースベクトル版(Instance)
   * 行列サイズ [i][j], ベクトルサイズ [j](縦ベクトル), 結果サイズ[i](縦ベクトル,密ベクトル)
   */
  public static boolean Mv(float[][] mat, Instance vec, float[] target) {
    int size = vec.getSize();
    int maxIndex = vec.getIndex(size-1);

    if (mat.length == 0 || mat[0].length == 0
        || mat[0].length < maxIndex || mat.length != target.length) {
      return false;
    }

    for (int i=0; i < mat.length; ++i) {
      float sum = 0.0f;
      for (int j=0; j < size; ++j) {
        sum += mat[i][vec.getIndex(j)] * vec.getWeight(j);
      }
      target[i] = sum;
    }
    return true;
  }

  /**
   * ベクトルx行列
   * ベクトルサイズ [i](横ベクトル), 行列サイズ [i][j], 結果サイズ[j](横ベクトル)
   */
  public static boolean vM(float[] vec, float[][] mat, float[] target) {
    if (mat.length == 0 || mat[0].length == 0
        || mat.length != vec.length || mat[0].length != target.length) {
      return false;
    }
    Arrays.fill(target, 0.0f);
    for (int i=0; i < mat.length; ++i) {
      float veci = vec[i];
      for (int j=0; j < mat[0].length; ++j) {
        target[j] += mat[i][j] * veci;
      }
    }
    return true;
  }

  /**
   * ベクトルx行列 スパースベクトル版(Instance)
   * ベクトルサイズ [i](横ベクトル), 行列サイズ [i][j], 結果サイズ[j](横ベクトル)
   */
  public static boolean vM(Instance vec, float[][] mat, float[] target) {
    int size = vec.getSize();
    int maxIndex = vec.getIndex(size-1);

    if (mat.length == 0 || mat[0].length == 0
        || mat.length < maxIndex || mat[0].length != target.length) {
      return false;
    }
    Arrays.fill(target, 0.0f);
    for (int i=0; i < size; ++i) {
      float veci = vec.getWeight(i);
      int row = vec.getIndex(i);
      for (int j=0; j < mat[0].length; ++j) {
        target[j] += mat[row][j] * veci;
      }
    }
    return true;
  }

  /**
   * ベクトル x ベクトル（外積）
   * ベクトルサイズ [i](縦ベクトル) x ベクトルサイズ[j]（横ベクトル） = 行列サイズ [i][j]
   */
  public static boolean vecCross(float[] Vvec, float[] Hvec, float[][] target) {
    if (target.length == 0 || target.length != Vvec.length || target[0].length != Hvec.length) {
      return false;
    }
    for (int i=0; i<Vvec.length; ++i) {
      for (int j=0; j<Hvec.length; ++j) {
        target[i][j] = Vvec[i] * Hvec[j];
      }
    }
    return true;
  }

  /**
   * ベクトル ・ ベクトル（内積）
   */
  public static float vecDot(float[] Vvec, float[] Hvec) {
    float sum = 0.0f;
    if (Vvec.length == Hvec.length) {
      for (int i=0; i<Vvec.length; ++i) {
        sum += Vvec[i] * Hvec[i];
      }
    }
    return sum;
  }
  /**
   * ベクトル ・ ベクトル（内積）
   * 一方がスパースベクトルの時
   */
  public static float vecDot(Instance i, float[] vec) {
    float sum = 0.0f;
    int size = i.getSize();
    int maxIndex = i.getIndex(size-1);

    if (vec.length > maxIndex) {
      for (int j=0; j<size; ++j) {
        sum += i.getWeight(j) * vec[i.getIndex(j)];
      }
    }
    return sum;
  }
}
