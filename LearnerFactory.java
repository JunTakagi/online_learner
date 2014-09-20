public class LearnerFactory {
  public static Learner get(String type) {
    return new PassiveAggressive(100);
  }
}
