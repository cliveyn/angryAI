package physics.materials;

import java.awt.Color;

public class Wood extends GameObject {
	
	public Wood(){
		
	}

	public Wood(double xsize, double ysize) {
		if (xsize == ysize) {
			if (xsize == 1) {
				this.health = 700;
			} else
				this.health = 1100;
		} else if (xsize > 1 && ysize > 2) {
			this.health = 1300;
		} else if (xsize == 10) {
			this.health = 1500;
		} else if (ysize == 10) {
			this.health = 1500;
		} else if (xsize > ysize) {
			this.health = 800 + (50 * xsize);
		} else if (ysize > xsize) {
			this.health = 800 + (50 * ysize);
		}
		this.health = 2000;
		maxHealth = health;
		this.color = new Color(153, 76, 0);
	}

}
