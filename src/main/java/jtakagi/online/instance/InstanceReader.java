package jtakagi.online.instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

public abstract class InstanceReader {
  BufferedReader br;
  boolean initialized;

  public InstanceReader() {
    this.br = null;
    this.initialized = false;
  }

  /**
   * ファイルのセット
   */
  public void begin(String file) throws IOException {
    br = new BufferedReader(new FileReader(new File(file)));
    initialized = true;
  }

  /**
   * 次のインスタンスを返す
   * @return 読み込んだ Instance。EOFのときはnull
   */
  public Instance getNext() throws IOException, NumberFormatException{
    if (! initialized) return null;
    String line = br.readLine();
    if (line == null) {
      initialized = false;
      return null;
    }
    return createInstance(line);
  }

  public abstract Instance createInstance(String line) throws NumberFormatException;
}
