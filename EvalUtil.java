import java.util.List;

public class EvalUtil {

  public float calcAccuracy(List<PredictAndLabel> list) {
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
   * AUCなどを計算するためのデータ
   */
  public class ScoreAndLabel {
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
  public class PredictAndLabel {
    private boolean predict;
    private boolean label;

    public PredictAndLabel(boolean predict, boolean label) {
      this.predict = predict;
      this.label = label;
    }

    public boolean getPredict() { return predict; }
    public boolean getLabel() { return label; }
  }
}

