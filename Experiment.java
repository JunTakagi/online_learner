
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Experiment {
  public static void main(String args[]) throws Exception {
    String train = args[0];
    String test = args[1];
    int repeat = Integer.parseInt(args[2]);

    Learner learner = LearnerFactory.get(args[3]);
    String[] learnerOpts = Arrays.copyOfRange(args, 4, args.length);
    learner.setup(learnerOpts);

    InstanceReader trainset = new LibSVMInstanceReader();
    InstanceReader testset = new LibSVMInstanceReader();

    Instance instance = null;

    List<EvalUtil.PredictAndLabel> predictions = new ArrayList<EvalUtil.PredictAndLabel>();
    for (int i=0; i<repeat; ++i) {
      trainset.begin(train);
      while ((instance = trainset.getNext()) != null) {
        learner.update(instance);
      }
      predictions.clear();
      testset.begin(test);
      while ((instance = testset.getNext()) != null) {
        predictions.add(new EvalUtil.PredictAndLabel(learner.predict(instance), instance.getLabel()));
      }
      float accuracy = EvalUtil.calcAccuracy(predictions);
      System.out.println("[" + i + "] : " + accuracy);
    }
  }
}
