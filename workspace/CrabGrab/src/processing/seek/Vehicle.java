
package processing.seek;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class Vehicle
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Vehicle(PApplet p, float px, float py)
	{
		parent = p;

		this.x = px;
		this.y = py;

		acceleration = new PVector(0, 0);
		velocity = new PVector(0, 0);
		location = new PVector(x, y);

		r = 8.0f;

		maxspeed = 4.0f;
		maxforce = 0.1f;

		// crab
		// change to relative path
		spritesheet = parent.loadImage("C:\\Users\\CRABGRAB\\Desktop\\DEV\\workspace\\CrabGrab\\bin\\processing\\anim_crab.png");
		parent.noStroke();
		parent.textureMode(PConstants.NORMAL);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void update()
	{
		velocity.add(acceleration);
		velocity.limit(maxspeed);
		location.add(velocity);
		acceleration.mult(0);
	}

	public void seek(PVector target)
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
		applyForce(steer);
	}

	public void display()
	{
		float theta = (float) (velocity.heading() + Math.PI / 2);

		f = parent.frameCount / 4;
		int fi = f + 1;
		float x = fi % DIM * W;
		float y = fi / DIM % DIM * H;

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

		/*
		 * parent.fill(175); parent.stroke(0);
		 *
		 * parent.pushMatrix(); parent.translate(location.x, location.y);
		 * parent.rotate(theta); parent.beginShape(); parent.vertex(0, -r * 2);
		 * parent.vertex(-r, r * 2); parent.vertex(r, r * 2);
		 * parent.endShape(PConstants.CLOSE); parent.popMatrix();
		 */
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

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private PApplet parent;

	private PVector location;
	private PVector velocity;
	private PVector acceleration;

	private float x;
	private float y;

	private float r;
	private float maxspeed;
	private float maxforce;

	private PImage spritesheet;
	private int DIM = 4; // spritesheet 4x4
	private float W = 1.0f / DIM;
	private float H = 1.0f / DIM;
	private int f;

}
