package planner;

import java.util.ArrayList;

public class TestingClass {

	public static void main(String[] args) {
		// this is a testing method
		PlannerStringConverter bla = new PlannerStringConverter();
		ArrayList<DecisionObject> blub = bla.convertString(
				"[[[midStoneBoulder3],[midStoneBoulder3,midStoneBoulder3,midWoodBoulder3,pig_3,midStoneBoulder2,midStoneBoulder2,midWoodBoulder2,pig_2]],[[pig_3],[pig_3]],[[pig_3],[pig_3]]]");
		for (DecisionObject deci : blub) {
			System.out.println(blub.toString());
		}
	}

}
