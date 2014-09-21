import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.StringTokenizer;


public abstract class Learner {
  protected float[] weights;

  public Learner(int size) {
    this.weights = new float[size];
    Arrays.fill(weights, 0.0f);
  }

  public Learner(int size, String file) throws IOException, NumberFormatException {
    this.weights = new float[size];
    Arrays.fill(weights, 0.0f);

    BufferedReader br = new BufferedReader(
                            new FileReader(new File(file)));
    String line = null;
    while((line = br.readLine()) != null) {
      StringTokenizer st = new StringTokenizer("\t", line);
      if (st.countTokens() != 2) {
        throw new RuntimeException("weight reading error : cannot read line " + line);
      }
      int index = Integer.parseInt(st.nextToken());
      float weight = Float.parseFloat(st.nextToken());
      if (index > size) {
        throw new RuntimeException("weight reading error : index size over " + line);
      }
      weights[index] = weight;
    }
  }

  /**
   * @return 更新したかどうか
   */
  public abstract boolean update(Instance i);

  /**
   * @return 内積
   */
  public float dot(Instance i) {
    int size = i.getSize();
    if (i.getIndex(size-1) > weights.length) {
      throw new RuntimeException(i.getSize() + " is over the learner size " + weights.length);
    }

    float dotProduct = 0.0f;
    for (int ind=0; ind<size; ind++) {
      dotProduct += weights[i.getIndex(ind)] + i.getWeight(ind);
    }
    return dotProduct;
  }

  /**
   * スパースベクトルとの足し算
   */
  public void add(Instance i) {
    int size = i.getSize();
    if (i.getIndex(size-1) > weights.length) {
      throw new RuntimeException(i.getSize() + " is over the learner size " + weights.length);
    }

    for (int ind=0; ind<size; ind++) {
      weights[i.getIndex(ind)] += i.getWeight(ind);
    }
  }


  /**
   * パラメータなどのセットアップメソッド
   * デフォルトでは何もしない
   */
  public void setup(String[] args) {}

  /**
   * @return 予測スコア。デフォルトでは内積
   */
  public float score(Instance i) {
    return dot(i);
  }

  /**
   * @return 予測ラベル。デフォルトではscoreの符号で判定
   */
  public boolean predict(Instance i) {
    return (score(i) > 0.0f);
  }
}
