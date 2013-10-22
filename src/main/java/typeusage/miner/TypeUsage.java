package typeusage.miner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import soot.Type;

/** 
 * is an abstraction over Soot locals:
 * Variable: 1 ----- 1..n Local
*
*/
public class TypeUsage {
  String location = "!";
  String type = "!";
  Type sootType = null;
  String methodContext = "!";
  List<MethodCall> underlyingLocals = new ArrayList<MethodCall>();
  private final Set<String> methodCalls = new HashSet<String>();
  Set<String> _extends = new HashSet<String>();

  @Override
  public int hashCode() {
    return -1;
  };

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof TypeUsage))
      return false;
    TypeUsage other = (TypeUsage) obj;
    return other.type.equals(this.type) && other.methodCalls.equals(this.methodCalls);
  };


  public void addMethodCall(String s) {
    methodCalls.add("call:" + s);
  }

  public TypeUsage(String _methodContext) {
    methodContext = _methodContext;
  }

  public TypeUsage() {
  }

  public TypeUsage call(String call) {
    addMethodCall(call);
    return this;
  }

  public TypeUsage type(String type) {
    this.type = type;
    return this;
  }

  @Override
  public String toString() {
    return repTypeMethodCalls();
  }

  public String repLocationContextTypeMethodCalls() {
    String sep = " ";
    return "location:" + location + sep + repContextTypeMethodCalls() + sep + StringUtils.join(_extends, sep);
  }

  public String repContextTypeMethodCalls() {
    String sep = " ";
    return "context:" + methodContext + sep + repTypeMethodCalls();
  }

  public String repTypeMethodCalls() {
    String sep = " ";
    return "type:" + type + sep + repMethodCalls();
  }

  public String repMethodCalls() {
    String sep = " ";
    return StringUtils.join(methodCalls, sep);
  }

}
