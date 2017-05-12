package planner;

import java.io.File;
import java.nio.file.Path;

public class VerificationHelper {
	void verify(Path path) throws PlannerException{
		if(path == null) throw new PlannerException("Given path was null.");
	}

	public void validate(File levelFile) throws PlannerException {
		if(!levelFile.isAbsolute()) throw new PlannerException("File path not absolute.");
		if(!levelFile.exists()) throw new PlannerException("Level-File does not exist.");
		if(!levelFile.isFile()) throw new PlannerException("Given file is a directory or otherwise not normal.");
		
		if(!levelFile.getName().endsWith(".pl")) throw new PlannerException("File is not of .pl-type");
	}
}
