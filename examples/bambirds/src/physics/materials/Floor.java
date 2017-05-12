package physics.materials;

import java.awt.Color;

public class Floor extends GameObject {
	public Floor() {
		this.color = new Color(51, 102, 0);
		this.setHealth(Double.MAX_VALUE);
	}

}
