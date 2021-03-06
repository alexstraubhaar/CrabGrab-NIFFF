
package processing.flocking;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

/**
 *
 * @author Alexandre Straubhaar
 *
 *         This class describes the behavior of a Boid (element of the flock).
 *
 */
public class Boid
{
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Boid(PApplet p, float x, float y)
	{
		parent = p;
		// initialisation
		acceleration = new PVector(0, 0);
		new PVector();
		velocity = PVector.random2D();
		setLocation(new PVector(x, y));

		r = 80.0f;
		maxspeed = 2.0f;
		maxforce = 0.03f;

		spritesheet = parent.loadImage(
				"C:\\Users\\User\\Documents\\He-Arc\\3\\NIFFF\\TB\\doc\\Krabbling\\anim_crab_rotated.png");

		parent.noStroke();
		parent.textureMode(PConstants.NORMAL);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void run(ArrayList<Boid> boids, boolean seekMouse)
	{
		flock(boids, seekMouse);
		update();
		borders();
		render();
	}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void applyForce(PVector force)
	{
		acceleration.add(force);
	}

	private void render()
	{
		// draw a crab in the velocity direction
		theta = (float) (velocity.heading() + Math.toRadians(90.0));

		// se baser sur le temps �coul� depuis le lancement
		f = parent.frameCount / 4;
		int fi = f + 1;
		float x = fi % DIM * W;
		float y = fi / DIM % DIM * H;

		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);
		parent.beginShape();
		parent.texture(spritesheet);
		parent.vertex(0, 0, x, y);
		parent.vertex(100, 0, x + W, y);
		parent.vertex(100, 100, x + W, y + H);
		parent.vertex(0, 100, x, y + H);
		parent.endShape();
		parent.popMatrix();
	}

	// pacman style borders
	private void borders()
	{
		if (getLocation().x < -r)
		{
			getLocation().x = parent.width + r;
		}
		if (getLocation().y < -r)
		{
			getLocation().y = parent.height + r;
		}
		if (getLocation().x > parent.width + r)
		{
			getLocation().x = -r;
		}
		if (getLocation().y > parent.height + r)
		{
			getLocation().y = -r;
		}
	}

	private void update()
	{
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		getLocation().add(velocity);
		acceleration.mult(0.0f);
	}

	// applying rules
	private void flock(ArrayList<Boid> boids, boolean seekMouse)
	{
		PVector sep = separate(boids);
		PVector ali = align(boids);
		PVector coh = cohesion(boids, seekMouse);

		// poids arbitraire sur les forces
		sep.mult(1.5f);
		ali.mult(1.0f);
		coh.mult(2.0f);

		// ajout des vecteurs de force � l'acc�leration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);
	}

	// follow the flock
	private PVector cohesion(ArrayList<Boid> boids, boolean seekMouse)
	{

		float neighbordist = 50.0f;
		PVector sum = new PVector(0, 0);
		int count = 0;

		for (Boid other : boids)
		{
			float d = PVector.dist(getLocation(), other.getLocation());
			if ((d > 0) && (d < neighbordist))
			{
				sum.add(other.getLocation());
				count++;
			}
		}

		if (count > 0)
		{
			sum.div(count);
			return seek(new PVector(parent.mouseX, parent.mouseY));
		} else
		{
			return new PVector(0, 0);
		}
	}

	// average velocity calculation
	private PVector align(ArrayList<Boid> boids)
	{
		float neighbordist = 50.0f;
		PVector sum = new PVector(0, 0);
		int count = 0;

		for (Boid other : boids)
		{
			float d = PVector.dist(getLocation(), other.getLocation());
			if ((d > 0) && (d < neighbordist))
			{
				sum.add(other.velocity);
				count++;
			}
		}

		if (count > 0)
		{
			sum.div(count);
			sum.setMag(maxspeed);
			PVector steer = PVector.sub(sum, velocity);
			steer.limit(maxforce);
			return steer;
		} else
		{
			return new PVector(0, 0);
		}
	}

	// avoids collision between boids
	private PVector separate(ArrayList<Boid> boids)
	{
		float desiredseparation = 50.0f;
		PVector steer = new PVector(0, 0, 0);
		int count = 0;

		// too close ?
		for (Boid other : boids)
		{
			float d = PVector.dist(getLocation(), other.getLocation());
			if ((d > 0) && (d < desiredseparation))
			{
				PVector diff = PVector.sub(getLocation(), other.getLocation());
				diff.normalize();
				diff.div(d);
				steer.add(diff);
				count++;
			}
		}

		if (count > 0)
		{
			steer.div(count);
		}

		if (steer.mag() > 0)
		{
			steer.setMag(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}

		return steer;
	}

	/**
	 * Crabs seek random position, they follow the flock
	 *
	 * @param target
	 * @return direction (steer)
	 */
	public PVector seek(PVector target)
	{
		/*
		 * PVector desired = PVector.sub(target, getLocation());
		 * desired.setMag(maxspeed);
		 *
		 * PVector steer = PVector.sub(desired, velocity);
		 * steer.limit(maxforce); return steer;
		 */
		return seekMouse(target);
	}

	/**
	 * Crabs are now seeking the position of the mouse if it is in the area
	 * (window)
	 *
	 * @param target
	 */
	public PVector seekMouse(PVector target)
	{
		PVector desired = PVector.sub(target, location);
		float d = desired.mag();
		desired.normalize();

		if (d < 100)
		{
			float m = PApplet.map(d, 0, 100, 0, maxspeed);
			desired.mult(m);
		} else
		{
			desired.mult(maxspeed);
		}

		PVector steer = PVector.sub(desired, velocity);
		steer.limit(maxforce);
		return steer;
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	public PVector getLocation()
	{
		return location;
	}

	public void setLocation(PVector location)
	{
		this.location = location;
	}

	// parent class
	private PApplet parent;

	private PVector location;
	private PVector velocity;
	private PVector acceleration;

	private float r;
	private float maxforce;
	private float maxspeed;
	private float theta;

	// crab
	private PImage spritesheet;
	private int DIM = 4; // spritesheet 4x4
	private float W = 1.0f / DIM;
	private float H = 1.0f / DIM;
	private int f;
}
