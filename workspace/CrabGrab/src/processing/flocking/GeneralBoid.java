
package processing.flocking;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

/**
 *
 * @author Alexandre Straubhaar
 *
 *         This class describes the behavior of a Boid (element of the flock).
 *         It is adapted so Crab and other types of Boid can extend from
 *         it.
 *
 */
public class GeneralBoid
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public GeneralBoid(PApplet p, float x, float y)
	{
		parent = p;
		// initialisation
		acceleration = new PVector(0, 0);
		new PVector();
		velocity = PVector.random2D();
		setLocation(new PVector(x, y));

		r = 8.0f;
		maxspeed = 2.0f;
		maxforce = 0.03f;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void run(ArrayList<GeneralBoid> boids)
	{
		flock(boids);
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
		// draw a triangle in the velocity direction
		theta = (float) (velocity.heading() + Math.toRadians(90.0));

		parent.fill(200, 100.0f);
		parent.stroke(255);
		parent.pushMatrix();
		parent.translate(getLocation().x, getLocation().y);
		parent.rotate(theta);
		parent.beginShape(PConstants.TRIANGLES);
		parent.vertex(0, -r * 2);
		parent.vertex(-r, r * 2);
		parent.vertex(r, r * 2);
		parent.endShape();
		parent.popMatrix();
	}

	// pacman style borders
	protected void borders()
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

	protected void update()
	{
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		getLocation().add(velocity);
		acceleration.mult(0.0f);
	}

	// rules application
	protected void flock(ArrayList<GeneralBoid> boids)
	{
		PVector sep = separate(boids);
		PVector ali = align(boids);
		PVector coh = cohesion(boids);

		// poids arbitraire sur les forces
		sep.mult(1.5f);
		ali.mult(1.0f);
		coh.mult(1.0f);

		// ajout des vecteurs de force � l'acc�leration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);
	}

	// follow the flock
	private PVector cohesion(ArrayList<GeneralBoid> boids)
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
	private PVector align(ArrayList<GeneralBoid> boids)
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
	private PVector separate(ArrayList<GeneralBoid> boids)
	{
		float desiredseparation = 35.0f;
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

	public PVector seek(PVector target)
	{
		PVector desired = PVector.sub(target, getLocation());
		desired.setMag(maxspeed);

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
	protected PVector velocity;
	private PVector acceleration;

	private float r;
	private float maxforce;
	private float maxspeed;
	protected float theta;
}
