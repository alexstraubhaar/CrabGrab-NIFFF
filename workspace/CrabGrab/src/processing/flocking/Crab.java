
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
		outOfBounds = false;

		r = 80.0f;
		maxforce = 0.03f;

		// animation and render
		this.spritesheet = spritesheet;
		offset = (int) parent.random(DIM * DIM);
		zoomValue = 0.0f;

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
	public static void setHumanNeigh(float new_humanneigh)
	{
		humanNeighCoh = new_humanneigh;
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

	// outOfBounds ?
	public boolean getOutOfBounds()
	{
		return outOfBounds;
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

		// TODO
		// is used to adapt animation speed and movement
		// float value = ((velocity.mag() / maxspeed));

		f = (int) (parent.frameCount / 0.5f);

		int fi = f + offset;
		float x = fi % DIM * W;
		float y = fi / DIM % DIM * H;

		float finalSize = size * sizeFactor;

		// makes crabs appear
		if (zoomValue < 1.0f)
		{
			zoomValue += 0.05;
		}

		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);
		parent.scale(zoomValue);
		parent.beginShape();
		parent.texture(spritesheet);
		parent.vertex(-finalSize, -finalSize, x, y);
		parent.vertex(finalSize, -finalSize, x + W, y);
		parent.vertex(finalSize, finalSize, x + W, y + H);
		parent.vertex(-finalSize, finalSize, x, y + H);
		parent.endShape();
		parent.popMatrix();

	}

	// out of bounds detection
	@Override
	void borders()
	{
		if (location.x < -r || location.y < -r || location.x > parent.width + r
				|| location.y > parent.height + r)
		{
			outOfBounds = true;
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
		float neighbordist = 0.0f;
		PVector sum = new PVector(0, 0);
		int count = 0;

		for (GeneralBoid other : boids)
		{
			// human or crab ?
			if (other.getClass() == Crab.class)
			{
				neighbordist = neighborLimit;
			} else if (other.getClass() == Human.class)
			{
				neighbordist = humanNeighCoh;
			}

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
			} else if (other.getClass() == Human.class)
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

	// Trying the repel function
	public void repelForce(PVector obstacle, float radius)
	{
		PVector futPos = PVector.add(location, velocity);
		PVector dist = PVector.sub(obstacle, futPos);
		float d = dist.mag();

		if (d <= radius)
		{
			PVector repelVec = PVector.sub(location, obstacle);
			repelVec.normalize();
			if (d != 0)
			{
				repelVec.normalize();
				repelVec.mult(maxforce + 7);

				if (repelVec.mag() < 0)
				{
					repelVec.y = 0;
				}
			}
			applyForce(repelVec);
		}
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
	private boolean outOfBounds;
	private float zoomValue;

	// size
	private static int size;
	private float sizeFactor;
	private static float humanSepLimit;
	private static float humanNeighCoh;

	// weights of rules
	private static float wsep;
	private static float wali;
	private static float wcoh;
}
