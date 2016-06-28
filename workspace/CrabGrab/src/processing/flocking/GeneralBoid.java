
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

	abstract void render();

	// pacman style borders
	abstract void borders();

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
		sep.mult(wsep);
		ali.mult(wali);
		coh.mult(wcoh);

		// ajout des vecteurs de force � l'acc�leration
		applyForce(sep);
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
	protected float maxspeed;
	protected float theta;

	// weights
	protected float wsep;
	protected float wali;
	protected float wcoh;
}
