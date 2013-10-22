package typeusage.miner;

import java.util.HashMap;

import soot.Body;
import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Stmt;

/**
 * answers the question: do two locals point to the same instance field?
 * 
 * @author Martin Monperrus
 */
public class MayPointToTheSameInstanceField {

	Body body;
	HashMap<Value, SootField> pointsTo = new HashMap<Value, SootField>();

	public MayPointToTheSameInstanceField(Body b) {

		body = b;
	
		// simple resolving						
		for (Unit u: body.getUnits()) { // for each statement
			//System.out.println(u+" /// "+u.getClass());
			Stmt s = (Stmt)u;
			if (s instanceof AssignStmt) {
				AssignStmt ass = (AssignStmt)s;
				//System.out.println(ass);
				Value left = ass.getLeftOp();
				Value right = ass.getRightOp();
				if (right instanceof InstanceFieldRef) {
					pointsTo.put(
							   left, // a local (e.g.  JimpleLocal)
							   ((InstanceFieldRef) right).getField() // an InstanceFieldRef (e.g. JInstanceFieldRef)
							   );
				}

				// not necessary according to the specification as given by the test suite
//				if (left instanceof InstanceFieldRef) {
//					pointsTo.put(
//							   right, // a local (e.g.  JimpleLocal)
//							   ((InstanceFieldRef) left).getField() // an InstanceFieldRef (e.g. JInstanceFieldRef)
//							   );
//				}

			}
		}
	}
	
	public boolean mayPointsToTheSameInstanceField(Value v1, Value v2) {
		if (!pointsTo.keySet().contains(v1)) { return false; }
		if (!pointsTo.keySet().contains(v2)) { return false; }		
		return pointsTo.get(v1).equals(pointsTo.get(v2));
	}
	
}
