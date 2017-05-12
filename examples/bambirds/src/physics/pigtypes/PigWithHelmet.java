package physics.pigtypes;

import java.awt.Color;

public class PigWithHelmet extends PigFacade {

	public PigWithHelmet() {
		// randomly generate the color
		health = 10350;
		damageThreshold = 2;
		this.color = new Color(51, 110, 0);
	}

}
