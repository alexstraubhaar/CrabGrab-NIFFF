
package processing.textureanim;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class TextureAnim
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public TextureAnim(PApplet p)
	{
		parent = p;
		spritesheet = parent.loadImage(
				"C:\\Users\\User\\Documents\\He-Arc\\3\\NIFFF\\TB\\doc\\Krabbling\\anim_crab.png");
		parent.textureMode(PConstants.NORMAL);
		parent.noStroke();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void settings()
	{
		parent.smooth(6);
	}

	public void draw()
	{
		int frameCount = parent.frameCount;
		int dmod = frameCount % 510;
		int col = dmod < 255 ? dmod : 510 - dmod;
		int width = parent.width;
		int height = parent.height;
		int mouseX = parent.mouseX;
		int mouseY = parent.mouseY;

		parent.beginShape();
		parent.fill(255, col, 0);
		parent.vertex(0, 0);
		parent.vertex(width, 0);
		parent.fill(0, 255, 255 - col);
		parent.vertex(width, height);
		parent.vertex(0, height);
		parent.endShape();

		parent.randomSeed(0);
		for (int i = 0; i < NUMSHAPES; i++)
		{
			parent.pushMatrix();
			float px = parent.random(width);
			float py = parent.random(height);
			parent.translate(px, py);
			parent.scale(PApplet.map(PApplet.dist(px, py, mouseX, mouseY), 0,
					width, 150, 15));
			int fi = frameCount + i;
			float x = fi % DIM * W;
			float y = fi / DIM % DIM * H;
			parent.beginShape();
			parent.texture(spritesheet);
			parent.vertex(0, 0, x, y);
			parent.vertex(1, 0, x + W, y);
			parent.vertex(1, 1, x + W, y + H);
			parent.vertex(0, 1, x, y + H);
			parent.endShape();
			parent.popMatrix();
		}
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

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	private PApplet parent;

	private PImage spritesheet;
	private int DIM = 4;
	private int NUMSHAPES = 1;
	private float W = 1.0f / DIM;
	private float H = 1.0f / DIM;
}
