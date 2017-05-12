package planner;

public class PlannerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 29520162008L;

	public PlannerException() {
		super();
	};
	
	PlannerException(String message, Throwable cause){
		super(message, cause);
	};
	
	PlannerException(String message){
		super(message);
	}
}
