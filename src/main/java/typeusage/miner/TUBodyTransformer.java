package typeusage.miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.NullType;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.LocalMustAliasAnalysis;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLnPosTag;
import soot.toolkits.graph.ExceptionalUnitGraph;


/** is a typical Soot BodyTransformer. Sends the type-usages to an IMethodCallCollector */ 
public class TUBodyTransformer extends BodyTransformer {

  private IMethodCallCollector collector;

  public TUBodyTransformer(IMethodCallCollector m) {
    this.collector = m;
  }

  LocalMustAliasAnalysis aliasInfo;

  MayPointToTheSameInstanceField instanceFieldDetector;

  HashMap<SootField, TypeUsage> crossMethodData = new HashMap<SootField, TypeUsage>();

  @Override
  protected void internalTransform(Body body, String phase,
      @SuppressWarnings("rawtypes") Map options) {

    String methodContext = collector.translateSignature(body.getMethod());

    aliasInfo = new LocalMustAliasAnalysis(
        new ExceptionalUnitGraph(body));
    //		LocalMustNotAliasAnalysis aliasInfo2 = new LocalMustNotAliasAnalysis(
    //				new ExceptionalUnitGraph(body));

    List<MethodCall> lCalls = new ArrayList<MethodCall>();

    for (Unit u : body.getUnits()) { // for each statement
      Stmt s = (Stmt) u;
      collector.debug(s + "-" + s.getClass());
      if (s.containsInvokeExpr()) {
        InvokeExpr invokeExpr = s.getInvokeExpr();
        collector.debug(invokeExpr.toString());
        if (invokeExpr instanceof InstanceInvokeExpr
        //&& ! (invokeExpr instanceof SpecialInvokeExpr)
        ) {
          MethodCall elem = new MethodCall();
          elem.s = s;
          elem.v = (Local) ((InstanceInvokeExpr) invokeExpr).getBase();
          lCalls.add(elem);
          collector.debug(elem + " " + invokeExpr.getMethod().getDeclaringClass().getName());
        }
      }
    }

    instanceFieldDetector = new MayPointToTheSameInstanceField(body);

    // creating the variables
    List<TypeUsage> lVariables = new ArrayList<TypeUsage>();
    for (MethodCall call1 : lCalls) {
      TypeUsage correspondingTypeUsage = findTypeUsage(call1, lVariables);
      Type type = call1.v.getType();
      if (type instanceof NullType) {
        type = call1.s.getInvokeExpr().getMethod().getDeclaringClass().getType();
      }
      if (type instanceof NullType) {
        continue;
      }

      collector.debug("v: " + type);

      if (correspondingTypeUsage != null

          // if there is a cast this test avoids unsound data (e.g. two method calls of different classes
          // in the same type-usage)
          && correspondingTypeUsage.type.equals(type.toString())

      ) {
        correspondingTypeUsage.underlyingLocals.add(call1);
        InvokeExpr invokeExpr = call1.s.getInvokeExpr();
        correspondingTypeUsage.addMethodCall(collector.translateSignature(invokeExpr.getMethod()));
        collector.debug("adding " + call1 + " to " + correspondingTypeUsage);
      } else {

        TypeUsage aNewTypeUsage = new TypeUsage(methodContext);

        collector.debug("creating " + aNewTypeUsage + " with " + call1.v);

        String location = body.getMethod().getDeclaringClass().toString();
        SourceLnPosTag tag = (SourceLnPosTag) call1.s.getTag("SourceLnPosTag");
        if (tag != null) {
          location += ":" + tag.startLn();
        }
        LineNumberTag tag2 = (LineNumberTag) call1.s.getTag("LineNumberTag");
        if (tag2 != null) {
          location += ":" + tag2.getLineNumber();
        }

        aNewTypeUsage.location = location;

        aNewTypeUsage.underlyingLocals.add(call1);

        InvokeExpr invokeExpr = call1.s.getInvokeExpr();
        aNewTypeUsage.addMethodCall(collector.translateSignature(invokeExpr.getMethod()));

        if (type instanceof NullType) {
          aNewTypeUsage.type = invokeExpr.getMethod().getDeclaringClass().getType().toString();
          aNewTypeUsage.sootType = invokeExpr.getMethod().getDeclaringClass().getType();
        } else {
          aNewTypeUsage.type = type.toString();
          aNewTypeUsage.sootType = type;
        }
        setExtends(type, aNewTypeUsage);

        // adding the link to the field
        SootField sootField = instanceFieldDetector.pointsTo.get(call1.v);
        if (sootField != null) {
          crossMethodData.put(sootField, aNewTypeUsage);
        }

        lVariables.add(aNewTypeUsage);
      }

    }

    // output the variables
    for (TypeUsage aVariable : lVariables) {
      if ((aVariable.type.startsWith(collector.getPackagePrefixToKeep()))) {
        collector.receive(aVariable);
      }
    }

  } // end internalTransform

  private TypeUsage findTypeUsage(MethodCall call1, List<TypeUsage> lVariables) {

    SootField sootField = instanceFieldDetector.pointsTo.get(call1.v);
    if (sootField != null) {
      return crossMethodData.get(sootField);
    }

    for (TypeUsage aTypeUsage : lVariables) {
      for (MethodCall e : aTypeUsage.underlyingLocals) {
        if (call1.v == e.v) {
          collector.debug(call1.v + " is same as " + e.v);
          collector.debug(aTypeUsage.type + " <-> " + e.s.getInvokeExpr().getMethod().getDeclaringClass());
          return aTypeUsage;
        }

        if (aliasInfo.mustAlias(call1.v, call1.s, e.v, e.s)) {
          collector.debug(call1.v + " alias to " + e.v);
          return aTypeUsage;
        }
        if (instanceFieldDetector.mayPointsToTheSameInstanceField(call1.v, e.v)) {
          return aTypeUsage;
        }
      }

    }
    return null;
  }

  /** recursive method to get the real type 
   * */
  private void setExtends(Type type, TypeUsage aVariable) {
    if (type instanceof RefType) {
      SootClass sc = ((RefType) type).getSootClass();
      // adding the current type:
      if (!sc.toString().equals("java.lang.Object")) {
        aVariable._extends.add("extend:" + sc.toString());
      }
      if (sc.hasSuperclass()) {
        setExtends(sc.getSuperclass().getType(), aVariable);
      }
    }
  }

}
