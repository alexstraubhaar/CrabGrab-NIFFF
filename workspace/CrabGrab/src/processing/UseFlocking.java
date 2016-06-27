
package processing;

import processing.core.PApplet;
import processing.flocking.Boid;
import processing.flocking.Flock;

/**
 *
 * @author Alexandre Straubhaar
 *
 *         This class is a flocking example realized by Craig Reynolds
 *         https://processing.org/examples/flocking.html It extends PApplet to
 *         indicates that it needs to be run as a Processing application
 *
 */
public class UseFlocking extends PApplet
{
	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		String[] a = {"MAIN"};
		PApplet.runSketch(a, new UseFlocking());
	}

	Flock flock;
	int x, y;
	boolean seekMouse = false;

	@Override
	public void setup()
	{
		size(1280, 800, P2D);
		flock = new Flock();

		// peuplage
		for (int i = 0; i < 10; i++)
		{
			flock.addBoid(new Boid(this, width / 2, height / 2));
		}
	}

	public void settings()
	{
		// rendered using 2D OpenGL with P2D parameter

		// fullScreen(P2D);
	}

	@Override
	public void draw()
	{
		// Test if the cursor is outside the scene
		if (mouseX > 0 && mouseX < width && mouseY > 0 && mouseY < height)
		{
			seekMouse = true;
		} else
		{
			seekMouse = false;
		}
		background(100);
		flock.run(seekMouse);
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}
