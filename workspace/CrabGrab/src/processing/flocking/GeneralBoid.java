
package processing.flocking;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

/**
 *
 * @author Alexandre Straubhaar
 *
 *         This class describes the behavior of a Boid (element of the flock).
 *         It is adapted so Crab and other types of Boid can extend from it and
 *         interact based on the same set of rules. (sep, coh and align)
 *
 */

public abstract class GeneralBoid
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	protected void run(ArrayList<GeneralBoid> boids)
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

	protected void applyForce(PVector force)
	{
		acceleration.add(force);
	}

	// what do we want to display to represent this object
	abstract void render();

	// what happens near the edge of the window
	abstract void borders();

	// weight getters
	abstract float getWAli();
	abstract float getWSep();
	abstract float getWCoh();

	// speed getter
	abstract float getMaxSpeed();

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
		// differenciate crabs and humans for separation
		ArrayList<GeneralBoid> humans = new ArrayList<GeneralBoid>();
		ArrayList<GeneralBoid> crabs = new ArrayList<GeneralBoid>();

		for (GeneralBoid generalBoid : boids)
		{
			if (generalBoid.getClass() == Crab.class)
			{
				crabs.add(generalBoid);
			} else if (generalBoid.getClass() == Human.class)
			{
				humans.add(generalBoid);
			}
		}

		PVector sepHuman = separate(humans);
		PVector sepCrab = separate(crabs);
		PVector ali = align(boids);
		PVector coh = cohesion(boids);

		// poids arbitraire sur les forces
		sepHuman.mult(Human.getWSepStatic());
		sepCrab.mult(getWSep());
		ali.mult(getWAli());
		coh.mult(getWCoh());

		// adding forces vector to the acceleration
		applyForce(sepHuman);
		applyForce(sepCrab);
		applyForce(ali);
		applyForce(coh);
	}

	// follow the flock
	abstract PVector cohesion(ArrayList<GeneralBoid> boids);

	// average velocity calculation
	abstract PVector align(ArrayList<GeneralBoid> boids);

	// avoids collision between boids
	abstract PVector separate(ArrayList<GeneralBoid> boids);

	abstract PVector seek(PVector target);

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	protected PVector getLocation()
	{
		return location;
	}

	protected void setLocation(PVector location)
	{
		this.location = location;
	}

	// parent class
	protected PApplet parent;

	protected PVector location;
	protected PVector velocity;
	protected PVector acceleration;

	protected float r;
	protected float maxforce;
	protected static float maxspeed;
	protected float theta;

	protected static float neighborLimit;
	protected static float sepLimit;

}
