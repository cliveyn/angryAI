package physics.materials;

import java.awt.Color;

public class RollingStone extends GameObject {

	public RollingStone() {
		// randomly generate the color
		this.health = 3500;
		this.maxHealth = health;
		this.damageThreshold = 7500;
		this.color = new Color(96, 96, 96);
	}

}