
/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *     and the following disclaimer in the documentation and/or other materials provided with the
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or
 *     promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package physics.visualisation;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.JFrame;

import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;

import ab.vision.ABObject;
import physics.materials.GameObject;
import physics.worldFactory.WorldFactory;
import physics.worldFactory.WorldSettings;

/**
 *
 *
 * @author Team Physics (Stefan Gruss, Leon Martin, Robert Mueller, Sascha
 *         Riechel)
 */
public class PhysicsSimulation extends JFrame {
	/** The serial version id */
	private static final long serialVersionUID = 5663760293144882635L;

	/** The scale 45 pixels per meter */
	public static double SCALE;

	/** The conversion factor from nano to base */
	public static double NANO_TO_BASE;

	/** The canvas to draw to */
	protected Canvas canvas;

	/** The dynamics engine */
	protected World world;

	/** Wether the example is stopped or not */
	protected boolean stopped;

	// create the size of the window
	Dimension size = new Dimension(800, 600);

	/** The time stamp for the last iteration */
	protected long last;

	private double duration = 0.0;
	int counter = 0;

	/**
	 * Default constructor for the window
	 *
	 * @param simulationParameters
	 * @param objects
	 */
	public PhysicsSimulation(String[] simulationParameters) {
		super("Graphics2D Example");
		SCALE = Double.parseDouble(simulationParameters[0]);

		NANO_TO_BASE = Double.parseDouble(simulationParameters[1]);

		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add a window listener
		this.addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 *
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.
			 * WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				// before we stop the JVM stop the example
				stop();
				super.windowClosing(e);
			}
		});

		// create a canvas to paint to
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);

		// add the canvas to the JFrame
		this.add(this.canvas);

		// make the JFrame not resizable
		// (this way I dont have to worry about resize events)
		this.setResizable(false);

		// size everything
		this.pack();

		// make sure we are not stopped
		this.stopped = false;

	}

	/**
	 * Start active rendering the example.
	 * <p>
	 * This should be called after the JFrame has been shown.
	 */
	public void start() {
		// initialize the last update time
		this.last = System.nanoTime();
		// don't allow AWT to paint the canvas since we are
		this.canvas.setIgnoreRepaint(true);
		// enable double buffering (the JFrame has to be
		// visible before this can be done)
		this.canvas.createBufferStrategy(2);
		// run a separate thread to do active rendering
		// because we don't want to do it on the EDT

		// Thread thread = new Thread() {
		// @Override
		// public void run() {
		// perform an infinite loop stopped
		// render as fast as possible
		System.out.println("PHYSICS - Simulation started at: " + LocalDateTime.now().toLocalTime());
		// world.step(300);
		for (int i = 0; i < 150000; i++) {
			gameLoop();
			// makeGraphics();
			// you could add a Thread.yield(); or
			// Thread.sleep(long) here to give the
			// CPU some breathing room
		}

		System.out.println("PHYSICS - Simulation finished at: " + LocalDateTime.now().toLocalTime());

		// }
		//
		// };
		// // set the game loop thread to a daemon thread so that
		// // it cannot stop the JVM from exiting
		// thread.setDaemon(true);
		// // start the game loop
		// thread.start();
	}

	private void makeGraphics() {
		// TODO Auto-generated method stub
		// COMMENT OUT IF NO GRAPHICS
		// get the graphics object to render to
		Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();

		// before we render everything im going to flip the y axis and move the
		// origin to the center (instead of it being in the top left corner)
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
		AffineTransform move = AffineTransform.getTranslateInstance(100, -400);
		g.transform(yFlip);
		g.transform(move);

		// now (0, 0) is in the center of the screen with the positive x axis
		// pointing right and the positive y axis pointing up

		// render anything about the Example (will render the World objects)
		this.render(g);

		// dispose of the graphics object
		g.dispose();

		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost()) {
			strategy.show();
		}

		// Sync the display on some systems.
		// (on Linux, this fixes event queue problems)
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * The method calling the necessary methods to update the game, graphics,
	 * and poll for input.
	 */
	protected void gameLoop() {

		// COMMENT OUT IF NO GRAPHICS
		// get the graphics object to render to
		Graphics2D g = (Graphics2D) this.canvas.getBufferStrategy().getDrawGraphics();

		// before we render everything im going to flip the y axis and move the
		// origin to the center (instead of it being in the top left corner)
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
		AffineTransform move = AffineTransform.getTranslateInstance(100, -400);
		g.transform(yFlip);
		g.transform(move);

		// now (0, 0) is in the center of the screen with the positive x axis
		// pointing right and the positive y axis pointing up

		// render anything about the Example (will render the World objects)
		this.render(g);

		// dispose of the graphics object
		g.dispose();

		// // UNTIL HERE
		//
		// // blit/flip the buffer
		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost()) {
			strategy.show();
		}
		//
		// // Sync the display on some systems.
		// // (on Linux, this fixes event queue problems)
		Toolkit.getDefaultToolkit().sync();

		// update the World

		// get the current time
		long time = System.nanoTime();
		// get the elapsed time from the last iteration
		long diff = time - this.last;
		// set the last time
		this.last = time;
		// convert from nanoseconds to seconds
		double elapsedTime = diff / NANO_TO_BASE;
		// update the world with the elapsed time
		this.world.update(0.001);
		// System.out.println(world.getBody(2).getWorldCenter().getXComponent());
		duration += elapsedTime;
	}

	/**
	 * Renders the example.
	 *
	 * @param g
	 *            the graphics object to render to
	 */
	protected void render(Graphics2D g) {
		// lets draw over everything with a white background
		g.setColor(Color.WHITE);
		g.fillRect(-100, -200, size.width, size.height);

		// lets move the view up some
		g.translate(0.0, -1.0 * SCALE);

		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) {
			// get the object
			GameObject go = (GameObject) this.world.getBody(i);
			// draw the object
			go.render(g);
		}
	}

	public World createWorld(List<List<ABObject>> objects) {
		WorldFactory wf = new WorldFactory();
		Settings settings = new Settings();
		settings.setStepFrequency(1.0 / 2.0);
		world = wf.generateWorld(objects, new Rectangle());
		world.setSettings(settings);
		return world;
	}

	public World createWorld(List<List<ABObject>> objects, WorldSettings worldSettings) {
		WorldFactory wf = new WorldFactory();
		return this.world = wf.generateWorld(objects, new Rectangle());
	}

	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Stops the example.
	 */
	public synchronized void stop() {
		this.stopped = true;
	}

	/**
	 * Returns true if the example is stopped.
	 *
	 * @return boolean true if stopped
	 */
	public synchronized boolean isStopped() {
		return this.stopped;
	}
}
