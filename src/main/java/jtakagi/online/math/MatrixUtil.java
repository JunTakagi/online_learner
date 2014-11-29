package jtakagi.online.math;

import java.util.Arrays;

import jtakagi.online.instance.Instance;

/**
 * $B:GE,2=$5$l$F$$$J$$$H$j$"$($:$N7W;;MQ(B
 * $B%i%$%V%i%j$GCV$-49$($?$$(B
 */
public class MatrixUtil {

  /**
   * $B9TNs%3%T!<(B
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
   * $B9TNs(Bx$B%Y%/%H%k(B
   * $B9TNs%5%$%:(B [i][j], $B%Y%/%H%k%5%$%:(B [j]($B=D%Y%/%H%k(B), $B7k2L%5%$%:(B[i]($B=D%Y%/%H%k(B)
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
   * $B9TNs(Bx$B%Y%/%H%k(B $B%9%Q!<%9%Y%/%H%kHG(B(Instance)
   * $B9TNs%5%$%:(B [i][j], $B%Y%/%H%k%5%$%:(B [j]($B=D%Y%/%H%k(B), $B7k2L%5%$%:(B[i]($B=D%Y%/%H%k(B,$BL)%Y%/%H%k(B)
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
   * $B%Y%/%H%k(Bx$B9TNs(B
   * $B%Y%/%H%k%5%$%:(B [i]($B2#%Y%/%H%k(B), $B9TNs%5%$%:(B [i][j], $B7k2L%5%$%:(B[j]($B2#%Y%/%H%k(B)
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
   * $B%Y%/%H%k(Bx$B9TNs(B $B%9%Q!<%9%Y%/%H%kHG(B(Instance)
   * $B%Y%/%H%k%5%$%:(B [i]($B2#%Y%/%H%k(B), $B9TNs%5%$%:(B [i][j], $B7k2L%5%$%:(B[j]($B2#%Y%/%H%k(B)
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
   * $B%Y%/%H%k(B x $B%Y%/%H%k!J30@Q!K(B
   * $B%Y%/%H%k%5%$%:(B [i]($B=D%Y%/%H%k(B) x $B%Y%/%H%k%5%$%:(B[j]$B!J2#%Y%/%H%k!K(B = $B9TNs%5%$%:(B [i][j]
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
   * $B%Y%/%H%k(B $B!&(B $B%Y%/%H%k!JFb@Q!K(B
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
   * $B%Y%/%H%k(B $B!&(B $B%Y%/%H%k!JFb@Q!K(B
   * $B0lJ}$,%9%Q!<%9%Y%/%H%k$N;~(B
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
