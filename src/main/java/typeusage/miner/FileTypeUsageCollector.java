package typeusage.miner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileTypeUsageCollector extends TypeUsageCollector {

  BufferedWriter output;
  
  public FileTypeUsageCollector(String file) throws Exception {
    super();
    output = new BufferedWriter(new FileWriter(file));
  }

  final public List<TypeUsage> data = new ArrayList<TypeUsage>();

  @Override
  public void receive(TypeUsage t) {
    try {
      output.write(t.toString()+"\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    try {
      output.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
