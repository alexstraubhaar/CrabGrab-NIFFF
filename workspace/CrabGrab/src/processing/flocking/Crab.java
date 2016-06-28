
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
public class Crab extends GeneralBoid
{
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Crab(PApplet p, float x, float y, PImage spritesheet)
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

		this.spritesheet = spritesheet;
		offset = (int) parent.random(DIM * DIM);
		reverse = false;

		parent.noStroke();
		parent.textureMode(PConstants.NORMAL);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void run(ArrayList<GeneralBoid> boids, PVector target)
	{
		flock(boids, target);
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

	@Override
	protected void applyForce(PVector force)
	{
		acceleration.add(force);
	}

	@Override
	void render()
	{
		// draw a crab in the velocity direction
		theta = (float) (velocity.heading() + Math.toRadians(90.0));

		f = parent.frameCount / 10;

		int fi = f + offset;
		float x = fi % DIM * W;
		float y = fi / DIM % DIM * H;

		int i = fi % (DIM * DIM);
		/*
		 * float xinv = Math.abs(x - DIM + 1); float yinv = Math.abs(y - DIM +
		 * 1);
		 *
		 * if (i >= DIM * DIM - 1) { reverse = !reverse; } if (reverse) { x =
		 * xinv; y = yinv; }
		 */

		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);
		parent.beginShape();
		parent.texture(spritesheet);
		parent.vertex(-50, -50, x, y);
		parent.vertex(50, -50, x + W, y);
		parent.vertex(50, 50, x + W, y + H);
		parent.vertex(-50, 50, x, y + H);
		parent.endShape();
		parent.popMatrix();

	}

	// pacman style borders
	@Override
	void borders()
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

	@Override
	protected void update()
	{
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		getLocation().add(velocity);
		acceleration.mult(0.0f);
	}

	// applying rules
	private void flock(ArrayList<GeneralBoid> boids, PVector target)
	{
		PVector sep = separate(boids);
		PVector ali = align(boids);
		PVector coh = cohesion(boids);

		// poids arbitraire sur les forces
		sep.mult(1.5f);
		ali.mult(1.0f);
		coh.mult(1.5f);

		// ajout des vecteurs de force � l'acc�leration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);
	}

	// follow the flock
	@Override
	PVector cohesion(ArrayList<GeneralBoid> boids)
	{

		float neighbordist = 50.0f;
		PVector sum = new PVector(0, 0);
		int count = 0;

		for (GeneralBoid other : boids)
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
			return seek(sum);
		} else
		{
			return new PVector(0, 0);
		}
	}

	// average velocity calculation
	@Override
	PVector align(ArrayList<GeneralBoid> boids)
	{
		float neighbordist = 50.0f;
		PVector sum = new PVector(0, 0);
		int count = 0;

		for (GeneralBoid other : boids)
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
	@Override
	PVector separate(ArrayList<GeneralBoid> boids)
	{
		float desiredseparation = 50.0f;
		PVector steer = new PVector(0, 0, 0);
		int count = 0;

		// too close ?
		for (GeneralBoid other : boids)
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
	 * Crabs seek a defined target
	 *
	 * @param target
	 * @return direction (steer)
	 */
	@Override
	public PVector seek(PVector target)
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

	@Override
	public PVector getLocation()
	{
		return location;
	}

	@Override
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
	private int offset;
	private boolean reverse;

}
