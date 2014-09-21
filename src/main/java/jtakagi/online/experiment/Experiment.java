package jtakagi.online.experiment;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import jtakagi.online.instance.*;
import jtakagi.online.learner.*;
import jtakagi.online.evaluate.*;

public class Experiment {
  public static final String AUC_MODE_STR = "auc";
  public static final String ACCURACY_MODE_STR = "accuracy";

  public static final int AUC_MODE = 0;
  public static final int ACCURACY_MODE = 1;
  public static final int INVALID = -1;

  public static int mode = INVALID;
  public static Learner learner;
  public static List<EvalUtil.PredictAndLabel> predictions;
  public static List<EvalUtil.ScoreAndLabel> scores;


  public static void parseMode(String modeStr) {
    if (modeStr.equals(AUC_MODE_STR)) {
      mode = AUC_MODE;
    } else if (modeStr.equals(ACCURACY_MODE_STR)) {
      mode = ACCURACY_MODE;
    } else {
      throw new RuntimeException("mode is not specified : " + modeStr);
    }
  }

  public static void clearEval() {
    predictions.clear();
    scores.clear();
  }

  public static void eval(Instance instance) {
    switch (mode) {
    case ACCURACY_MODE:
      predictions.add(new EvalUtil.PredictAndLabel(learner.predict(instance), instance.getLabel()));
      break;
    case AUC_MODE:
      scores.add(new EvalUtil.ScoreAndLabel(learner.score(instance), instance.getLabel()));
      break;
    }
  }

  public static void outputEval(int i) {
    switch (mode) {
    case ACCURACY_MODE:
      float accuracy = EvalUtil.calcAccuracy(predictions);
      System.out.println("[" + i + "] : " + accuracy);
      break;
    case AUC_MODE:
      float auc = EvalUtil.calcAUC(scores);
      System.out.println("[" + i + "] : " + auc);
      break;
    }
  }

  public static void main(String args[]) throws Exception {
    parseMode(args[0]);

    String train = args[1];
    String test = args[2];
    int repeat = Integer.parseInt(args[3]);

    learner = LearnerFactory.get(args[4]);
    String[] learnerOpts = Arrays.copyOfRange(args, 5, args.length);
    learner.setup(learnerOpts);

    InstanceReader trainset = new LibSVMInstanceReader();
    InstanceReader testset = new LibSVMInstanceReader();

    Instance instance = null;

    predictions = new ArrayList<EvalUtil.PredictAndLabel>();
    scores = new ArrayList<EvalUtil.ScoreAndLabel>();
    for (int i=0; i<repeat; ++i) {
      trainset.begin(train);
      while ((instance = trainset.getNext()) != null) {
        learner.update(instance);
      }
      clearEval();
      testset.begin(test);
      while ((instance = testset.getNext()) != null) {
        eval(instance);
      }
      outputEval(i);
    }
  }
}
