package typeusage.miner;

import soot.SootMethod;

/** defines an object that can can discuss with TUBodyTransformer */
public interface IMethodCallCollector {
  /** receives a type usage */
  void receive(TypeUsage t);
  
  /** computes a signature at the required granularity:
   * name
   * name(paramType_1)
   * returnType name
   * etc.
   */
  String translateSignature(SootMethod meth);
  
  /** if a type-usage"s class' fully-qualified name starts with this prefix, keep it */
  String getPackagePrefixToKeep();

  /** poor man's applicative debug */
  void debug(String msg);
}
