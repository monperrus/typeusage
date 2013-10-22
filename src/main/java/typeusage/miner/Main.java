package typeusage.miner;

public class Main {

  public final static String DEFAULT_DIR = "./target/test-classes/";
			  
  public static void main(String[] args) throws Exception { 
//    TypeUsageCollector c = new TypeUsageCollector();
    FileTypeUsageCollector c = new FileTypeUsageCollector("output.dat");
    String toBeAnalyzed = null;
    if (args.length > 0) {
      toBeAnalyzed = args[0];
      if (args.length > 1) {
    	  c.setPrefix(args[1]);
      }
    } else {
      toBeAnalyzed = DEFAULT_DIR;
    }
    c.setDirToProcess(toBeAnalyzed);
    c.run();
    c.close();
  }

}
