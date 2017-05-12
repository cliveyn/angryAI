package physics.materials;

import java.awt.Color;

public class Stone extends GameObject {

	public Stone(double xsize, double ysize) {

		// TODO Check if size are correct from feature extraction
		if (xsize == ysize) {
			if (xsize == 1) {
				this.health = 800;
			} else
				this.health = 1100;
		} else if (xsize > 1 && ysize > 2) {
			this.health = 1300;
		} else if (xsize > ysize) {
			this.health = 900 + (100 * xsize);
		} else if (ysize > xsize) {
			this.health = 900 + (100 * ysize);
		}
		this.health = 5000;
		this.maxHealth = this.health;

		this.color = new Color(150, 150, 150);
	}

	public Stone() {
		// TODO Auto-generated constructor stub
	}

}
