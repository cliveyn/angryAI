package physics.materials;

import java.awt.Color;

public class Ice extends GameObject {

	public Ice(double xsize, double ysize) {
		if (xsize == ysize) {
			if (xsize == 1) {
				this.health = 630;
			} else
				this.health = 900;
		} else if (xsize > 1 && ysize > 2) {
			this.health = 1030;
		} else if (xsize == 10) {
			this.health = 1150;
		} else if (ysize == 10) {
			this.health = 1150;
		} else if (xsize > ysize) {
			this.health = 690 + (35 * xsize);
		} else if (ysize > xsize) {
			this.health = 690 + (35 * ysize);
		}
		this.health = 1500;
		this.maxHealth = this.health;
		
		this.color = new Color(102, 178, 255);
	}

	public Ice() {
		// TODO Auto-generated constructor stub
	}

}
