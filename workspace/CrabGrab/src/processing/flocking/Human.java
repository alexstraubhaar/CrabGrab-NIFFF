
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
		acceleration = new PVector(0, 0);
		velocity = new PVector(0, 0);

		// rules weight
		wali = 0.0f;
		wcoh = 0.0f;
		wsep = 2.0f;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
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

	// no cohesion needed for now, just separation
	@Override
	PVector cohesion(ArrayList<GeneralBoid> boids)
	{
		return new PVector(0, 0);
	}

	// no need to align ^ / v
	@Override
	PVector align(ArrayList<GeneralBoid> boids)
	{
		return new PVector(0, 0);
	}

	@Override
	PVector separate(ArrayList<GeneralBoid> boids)
	{
		/*
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
		*/
		return new PVector(0,0);
	}

	// no need to seek for humans
	@Override
	PVector seek(PVector target)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	public static void setWAli(float new_wali)
	{
		wali = new_wali;
	}
	public static void setWSep(float new_wsep)
	{
		wsep = new_wsep;
	}
	public static void setWCoh(float new_wcoh)
	{
		wcoh = new_wcoh;
	}

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	@Override
	float getWAli()
	{
		return wali;
	}
	@Override
	float getWSep()
	{
		return wsep;
	}
	@Override
	float getWCoh()
	{
		return wcoh;
	}

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	public static float getWSepStatic()
	{
		return wsep;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// weights of rules
	private static float wali;
	private static float wsep;
	private static float wcoh;
	@Override
	float getMaxSpeed()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
