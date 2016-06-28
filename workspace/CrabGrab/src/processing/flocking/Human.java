
package processing.flocking;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

/**
 *
 * @author Alexandre Straubhaar
 *
 *         This class is used to make people interact with the crabs displayed,
 *         they too are boids but with their own rules and values.
 *
 */
public class Human extends GeneralBoid
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Human(PApplet p, float x, float y)
	{
		parent = p;
		location = new PVector(x, y);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

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
	void render()
	{
		// nothing to display
	}

	@Override
	void borders()
	{
		// no effect when near the borders
	}

	@Override
	PVector cohesion(ArrayList<GeneralBoid> boids)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	PVector align(ArrayList<GeneralBoid> boids)
	{
		// TODO Auto-generated method stub
		return null;
	}

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

	@Override
	PVector seek(PVector target)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// parent class
	private PApplet parent;

	private PVector location;

}
