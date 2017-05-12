package planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class PlannerStringConverter {

	private int decisionObjectLength;
	private ArrayList<DecisionObject> decisionObjectList;

	public PlannerStringConverter() {
		decisionObjectLength = 3;
		decisionObjectList = new ArrayList<>();
	}

	public ArrayList<DecisionObject> convertString(String stringToConvert) {
		decisionObjectList = new ArrayList<DecisionObject>();
		System.out.println("Given String (should not be empty): \"" + stringToConvert + "\"");
		try {
			// part the String in single plans
			String[] plans = stringToConvert.split(Pattern.quote("]],[["));
			for (String plan : plans) {
				// the array lists which will be in a single DecisionObject
				ArrayList<String> targets = new ArrayList<>();
				ArrayList<String> goals = new ArrayList<>();
				String strategy = "";
				String rankValue = "";

				// splits the plan in goals and targets
				String[] decisionArray = plan.split(Pattern.quote("],["));
				for (int i = 0; i < decisionObjectLength; i++) {
					decisionArray[i] = decisionArray[i]
							.replaceAll(Pattern.quote("[[["), "")
							.replaceAll(Pattern.quote("]"), "");
				}

				// adds the elements in the goal and target ArrayLists
				String[] targetsArray = decisionArray[0].split(",");
				Collections.addAll(targets, targetsArray);
				String[] goalsArray = decisionArray[1].split(",");
				Collections.addAll(goals, goalsArray);
				strategy = decisionArray[2];
				rankValue = decisionArray[3];

				// creates DecisionObjects out of the ArrayLists and adds them
				// to the output ArrayList
				if (!strategy.equals("dummy")){
					try {
						decisionObjectList.add(new DecisionObject(targets, goals, strategy, rankValue));
					} catch (PlannerException e1) {
						System.err.println(e1.getMessage());
					}
				}
			}
		} catch (Exception e) {
			System.err.println(
					"The String to convert did not match the expected format: [[[target1,target2],[goal1,goal2,...]],[[..],[..]]]");
			return decisionObjectList;
		}
		return decisionObjectList;
	}

	public void shutdown() {

	}
}