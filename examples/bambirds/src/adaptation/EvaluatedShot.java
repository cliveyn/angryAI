package adaptation;

import org.dyn4j.dynamics.World;

import ab.demo.other.Shot;

public class EvaluatedShot {
	private Shot shot;
	private double score;
	private World world;
	
	EvaluatedShot(Shot shot, double score, World world) {
		this.shot = shot;
		this.score = score;
		this.world = world;
	}
	
	/**
	 * Get the shot use in the simulation
	 * @return shot used in simulation
	 */
	public Shot getShot() {
		return shot;
	}
	
	/**
	 * Get the score that was achieved in the simulation according the the goals
	 * @return achieved score (range: [0.0 ... 1.0])
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * get the world in the 'after the shot state'
	 * @return world after shot
	 */
	public World getWorld() {
		return world;
	}
}
