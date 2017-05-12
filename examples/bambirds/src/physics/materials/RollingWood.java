package physics.materials;

import java.awt.Color;

public class RollingWood extends GameObject {

	public RollingWood() {
		// randomly generate the color
		this.health = 2000;
		this.maxHealth = health;
		this.damageThreshold = 7500;
		this.color = new Color(153, 76, 0);
	}

}