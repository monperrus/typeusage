package typeusage.tests;

import java.util.ArrayList;
import java.util.List;

import typeusage.miner.TypeUsage;
import typeusage.miner.TypeUsageCollector;


public class TestTypeUsageCollector extends TypeUsageCollector {

  public TestTypeUsageCollector() throws Exception {
    super();
  }

  final public List<TypeUsage> data = new ArrayList<TypeUsage>();

  @Override
  public void receive(TypeUsage t) {
    data.add(t);
  }

}
