
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class LibSVMInstanceReader extends InstanceReader {
  private Pattern spliter;

  public LibSVMInstanceReader() {
    spliter = Pattern.compile(":");
  }


  public Instance createInstance(String line) throws NumberFormatException {
    StringTokenizer st = new StringTokenizer(" \t", line);
    if (! st.hasMoreTokens()) {
      throw new RuntimeException("Lable not Found");
    }
    String labelStr = st.nextToken();
    boolean label = false;
    if (labelStr.equals("+1")) label = true;

    ArrayList<Float> weightslist = new ArrayList<Float>();
    ArrayList<Integer> indiceslist = new ArrayList<Integer>();

    while (st.hasMoreTokens()) {
      String[] kv = spliter.split(st.nextToken());
      if (kv.length != 2) {
        throw new RuntimeException("Cannot parse line " + line);
      }
      indiceslist.add(new Integer(kv[0]));
      weightslist.add(new Float(kv[1]));
    }

    int[] indices = new int[indiceslist.size()];
    float[] weights = new float[weightslist.size()];
    for (int i=0; i<indices.length; ++i) {
      indices[i] = indiceslist.get(i).intValue();
      weights[i] = weightslist.get(i).floatValue();
    }
    return new Instance(weights, indices, label);
  }
}

