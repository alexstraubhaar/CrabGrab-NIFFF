
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
 *         This class describes the behavior of a Crab which extends from
 *         GeneralBoid for interaction purposes.
 *
 */
public class Crab extends GeneralBoid
{
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Crab(PApplet p, float x, float y, float sizeFactor,
			PImage spritesheet)
	{
		// for using Processing functions
		parent = p;

		// movement
		acceleration = new PVector(0, 0);
		new PVector();
		velocity = PVector.random2D();
		setLocation(new PVector(x, y));

		r = 80.0f;
		maxforce = 0.03f;

		// animation and render
		this.spritesheet = spritesheet;
		offset = (int) parent.random(DIM * DIM);
		reverse = false;

		parent.noStroke();
		parent.textureMode(PConstants.NORMAL);

		this.sizeFactor = sizeFactor;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Set				*|
	\*------------------------------*/

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	// weights
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

	// size
	public static void setSize(int new_size)
	{
		size = new_size;
	}

	// neighbor
	public static void setNeighbor(float new_neighborlimit)
	{
		neighborLimit = new_neighborlimit;
	}
	public static void setSepLimit(float new_sepLimit)
	{
		sepLimit = new_sepLimit;
	}
	public static void setHumanSepLimit(float new_humanseplimit)
	{
		humanSepLimit = new_humanseplimit;
	}

	// speed
	public static void setMaxSpeed(float new_maxspeed)
	{
		maxspeed = new_maxspeed;
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

	@Override
	float getMaxSpeed()
	{
		return maxspeed;
	}

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

		float finalSize = size * sizeFactor;

		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);
		parent.beginShape();
		parent.texture(spritesheet);
		parent.vertex(-finalSize, -finalSize, x, y);
		parent.vertex(finalSize, -finalSize, x + W, y);
		parent.vertex(finalSize, finalSize, x + W, y + H);
		parent.vertex(-finalSize, finalSize, x, y + H);
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

	// follow the flock
	@Override
	PVector cohesion(ArrayList<GeneralBoid> boids)
	{
		float neighbordist = neighborLimit;
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
		float neighbordist = neighborLimit;
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
		float desiredseparation = 0.0f;
		PVector steer = new PVector(0, 0, 0);
		int count = 0;

		// too close ?
		for (GeneralBoid other : boids)
		{
			// human or crab ?
			if (other.getClass() == Crab.class)
			{
				desiredseparation = sepLimit;
			}
			else if (other.getClass() == Human.class)
			{
				desiredseparation = humanSepLimit;
			}

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
		desired.setMag(maxspeed);

		// minimal desired velocity
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

	// crab
	private PImage spritesheet;
	private int DIM = 4; // spritesheet 4x4
	private float W = 1.0f / DIM;
	private float H = 1.0f / DIM;
	private int f;
	private int offset;
	private boolean reverse;

	// size
	private static int size;
	private float sizeFactor;
	private static float humanSepLimit;

	// weights of rules
	private static float wsep;
	private static float wali;
	private static float wcoh;
}
