package planner;

import java.util.ArrayList;

public class DecisionObject {
	private ArrayList<String> targets = null;
	private ArrayList<String> goals = null;
	private String strategy = null;
	private String rankValue = null;
	
	DecisionObject (ArrayList<String> targets, ArrayList<String> goals, String strategy, String rankValue) throws PlannerException {
		validate(targets, goals);
		
		this.targets = targets;
		this.goals = goals;
		this.strategy = strategy;
		this.rankValue = rankValue;
	}
	
	/** Gives all the targets to be hit by one shot.
	 *  There is at least 1 target for any given action. For "blue"-birds e.g. there will be 3 targets, for white 2 etc.
	 * @return {@code ArrayList} of Targets, as {@code String}, which still have same identification as prior to planning.
	 */
	public ArrayList<String> getTargets() {
		return targets;
	}
	
	/** Gives all the goals respective to the targets of this {@Code DecisionObject}.
	 * In theory (based upon planner decisions) all goals should be destroyed by a shot to the target(s). 
	 * @return {@code ArrayList} of Targets, as {@code String}, which still have same identification as prior to planning.
	 */
	public ArrayList<String> getGoals() {
		return goals;
	}
	
	public String getStrategy() {
		return strategy;
	}
	
	public String getRank() {
		return rankValue;
	}
	
	private void validate(ArrayList<String> targetsToCheck, ArrayList<String> goalsToCheck) throws PlannerException{
		if(!isValid(targetsToCheck)) throw new PlannerException ("There was no Target given. Null Pointer or empty List.");
		if(!isValid(goalsToCheck)) throw new PlannerException ("There was no Goal given. Null Pointer or empty List.");
	}
	
	private boolean isValid(ArrayList<String> targetsOrGoals) throws PlannerException {
		if(targetsOrGoals == null) return false;
		if(targetsOrGoals.isEmpty()) return false; 
		return true;
	}
	
	public String toString(){
		return "Targets: "+targets.toString()+". Goals: "+goals.toString()+". Strategy: "+strategy+". Rank: "+rankValue;
	}
}
