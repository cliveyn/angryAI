package main;

import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

import physics.materials.Floor;
import physics.materials.Ice;
import physics.materials.RollingStone;
import physics.materials.Stone;
import physics.materials.TNT;
import physics.materials.Wood;

public class WorldEqualityComparator {
	private static double calculateTypeEqualityMetric(List<Body>b1, List<Body>b2){
		int c1 = 0;
		int c2 = 0;
		
		for(Body b : b1){
			if(b instanceof Floor || b instanceof Ice || b instanceof RollingStone || b instanceof Stone || b instanceof TNT || b instanceof Wood){
				c1++;
			}
		}
		
		for(Body b : b2){
			if(b instanceof Floor || b instanceof Ice || b instanceof RollingStone || b instanceof Stone || b instanceof TNT || b instanceof Wood){
				c2++;
			}
		}
		
		if(c1 == 0){
			return 1;
		}
		
		return 1 - c2/c1;
	}
	private static boolean isCounterpartObjectExisting(Body cb, List<Body>bs){
		for(Body b : bs){
			if(b != cb && b.getClass() == cb.getClass()){
				if(b.getMass() == cb.getMass() && b.getLocalCenter().x == cb.getLocalCenter().x && b.getLocalCenter().y == cb.getLocalCenter().y){
					return true;
				}
			}
		}
		
		return false;
	}
	public static double compareWorldsForEquality(World beforeWorld, World afterWorld){
		if(beforeWorld == null || afterWorld == null){
			return 0;
		}
		int matches = 0;
		for(Body b : beforeWorld.getBodies()){
			if(isCounterpartObjectExisting(b,afterWorld.getBodies())){
				matches++;
			}
		}
		
		double metric = calculateTypeEqualityMetric(beforeWorld.getBodies(), afterWorld.getBodies());
		
		return (metric + matches/beforeWorld.getBodies().size())/2;
	}
}
