package typeusage.miner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import soot.PackManager;
import soot.SootMethod;
import soot.Transform;
import soot.options.Options;

/**
 * This class makes a static anaylsis of a directory containing Java bytecode
 * using the Soot toolkit
 * 
 * It extracts all variables and their method calls
 * 
 * @author Martin Monperrus
 * 
 */
public class TypeUsageCollector implements IMethodCallCollector {

  /** The number of collected traces */
  int nbTraces;


  String filter = "";

  public TypeUsageCollector() throws Exception {
    Options.v().set_allow_phantom_refs(true);
  }

  public TypeUsageCollector run() throws Exception {

    PackManager.v().getPack("jtp")
        .add(new Transform("jtp.myTransform", new TUBodyTransformer(this)));

    Options.v().set_keep_line_number(true);
    Options.v().set_output_format(Options.output_format_none);

    String[] myArgs = {
        "-soot-classpath", getClassPath(),
        "-pp",// prepend is not required
        "-process-dir", getProcessDir(),
    };

    soot.Main.main(myArgs);
    return this;
  }

  final List<String> classpath = new ArrayList<String>();

  public String getClassPath() {
    classpath.add(getProcessDir());
    return StringUtils.join(classpath, ":");
  }

  @Override
  public void receive(TypeUsage t) {
    System.out.println(t);
  }

  //  mainObj.appOut_A.write(aVariable.repTypeMethodCalls() + "\n");
  //  mainObj.appOut_B.write(aVariable.repContextTypeMethodCalls() + "\n");
  //  mainObj.appOut_C.write(aVariable

  @Override
  public String translateSignature(SootMethod meth) {
    // can also be meth.getSignature
    return meth.getName() + "()";
    // or             aVariable.addMethodCall(invokeExpr.getMethod().getName());

  }

  public String getProcessDir() {
    return dirToProcess;
  }

  @Override
  public String getPackagePrefixToKeep() {
    return filter;
  }

  public void addToClassPath(String jar) {
    if (!new File(jar).exists()) {
      throw new IllegalArgumentException(jar + " must be a valid file");
    }
    classpath.add(jar);
  }

  String dirToProcess;

  public void setDirToProcess(String dirToProcess) {
    this.dirToProcess = dirToProcess;
  }

  @Override
  public void debug(String msg) {
    // subclasses may override
  }

  public void setPrefix(String prefix) {
    this.filter = prefix;
  }
}
