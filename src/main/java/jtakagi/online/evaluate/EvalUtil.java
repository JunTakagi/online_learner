package jtakagi.online.evaluate;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EvalUtil {

  /**
   * accuracyを計算する
   */
  public static float calcAccuracy(List<PredictAndLabel> list) {
    int size = list.size();
    float denom = (float) size;
    int numer = 0;
    for (PredictAndLabel pal : list) {
      if (pal.getPredict() == pal.getLabel()) { ++numer; }
    }
    float accuracy = ((float) numer) / denom;
    return accuracy;
  }

  /**
   * aucを計算する
   * 面倒臭いから台形近似とかしない系プログラム
   */
  public static float calcAUC(List<ScoreAndLabel> list) {
    int neg = 0;
    int pos = 0;

    Collections.sort(list, new Comparator<ScoreAndLabel>() {
      public int compare(ScoreAndLabel a, ScoreAndLabel b) {
        float comp = b.getScore() - a.getScore(); // 昇順ソート
        if (comp > 0.0f) return 1;
        else if (comp < 0.0f) return -1;
        else return 0;
      }
    });

    float auc = 0.0f;
    for (ScoreAndLabel sal : list) {
      boolean label = sal.getLabel();
      if (label) {
        pos++;
      } else {
        auc += (float) pos;
        neg++;
      }
    }
    auc /= (float)(pos * neg);
    return auc;
  }

  /**
   * AUCなどを計算するためのデータ
   */
  public static class ScoreAndLabel {
    private float score;
    private boolean label;

    public ScoreAndLabel(float score, boolean label) {
      this.score = score;
      this.label = label;
    }

    public float getScore() { return score; }
    public boolean getLabel() { return label; }
  }

  /**
   * Accuracyなどを計算するためのデータ
   */
  public static class PredictAndLabel {
    private boolean predict;
    private boolean label;

    public PredictAndLabel(boolean predict, boolean label) {
      this.predict = predict;
      this.label = label;
    }

    public boolean getPredict() { return predict; }
    public boolean getLabel() { return label; }
  }


  // debug
  public static void main(String args[]) {
    ArrayList<ScoreAndLabel> l = new ArrayList<ScoreAndLabel>();
    l.add(new ScoreAndLabel(0.1f, false));
    l.add(new ScoreAndLabel(0.2f, false));
    l.add(new ScoreAndLabel(0.3f, false));
    l.add(new ScoreAndLabel(0.4f, false));
    l.add(new ScoreAndLabel(0.5f, false));
    l.add(new ScoreAndLabel(0.6f, false));
    l.add(new ScoreAndLabel(0.7f, false));
    l.add(new ScoreAndLabel(0.8f, true));
    l.add(new ScoreAndLabel(0.9f, true));
    l.add(new ScoreAndLabel(1.0f, true));
    System.out.println(calcAUC(l));
  }

}

