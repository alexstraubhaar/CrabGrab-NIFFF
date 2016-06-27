
package processing.flocking;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Crab extends GeneralBoid
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Crab(PApplet p, float x, float y)
	{
		super(p, x, y);
		parent = p;
		f = parent.frameCount;
		spritesheet = parent.loadImage(
				"C:\\Users\\User\\Documents\\He-Arc\\3\\NIFFF\\TB\\doc\\Krabbling\\anim_crab.png");
		parent.textureMode(PConstants.NORMAL);
		parent.noStroke();
		theta = 0.0f;
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void settings()
	{
		parent.smooth(6);
	}

	@Override
	public void run(ArrayList<GeneralBoid> boids)
	{
		super.flock(boids);
		super.update();
		super.borders();
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

	private void render()
	{
		theta = (float) (super.velocity.heading() + Math.toRadians(90.0));
		int fi = f + 1;
		float x = fi % DIM * W;
		float y = fi / DIM % DIM * H;

		parent.pushMatrix();
		parent.translate(super.getLocation().x, super.getLocation().y);
		parent.rotate(super.theta);
		parent.beginShape();
		parent.texture(spritesheet);
		parent.vertex(0, 0, x, y);
		parent.vertex(100, 0, x + W, y);
		parent.vertex(100, 100, x + W, y + H);
		parent.vertex(0, 100, x, y + H);
		parent.endShape();
		parent.popMatrix();
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private PApplet parent;

	private PImage spritesheet;
	private int DIM = 4; // spritesheet 4x4
	private float W = 1.0f / DIM;
	private float H = 1.0f / DIM;
	private int f;
	private float theta;

}
