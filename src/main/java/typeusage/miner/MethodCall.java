package typeusage.miner;

import soot.Local;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

/** represents a method call on top of Soot's Local and Stmt */
public class MethodCall {

	public Local v;
	public Stmt s;
	@Override
	public String toString() {
		InvokeExpr invokeExpr = s.getInvokeExpr();
		return v.toString()+" "+invokeExpr.getMethod().getName();
	}

}
