
package processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import processing.core.PApplet;
import processing.core.PImage;
import processing.flocking.Crab;
import processing.flocking.Flock;
import processing.kinect.KinectTracker;

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

	KinectTracker tracker;
	Flock flock;
	int x, y;
	boolean seekMouse = false;
	Mat matDest;
	Mat matSrc;
	Mat matCali;

	@Override
	public void setup()
	{
		size(1024, 768, P2D);

		tracker = new KinectTracker(this);
		flock = new Flock();
		PImage spritesheet = loadImage(
				"C:\\Users\\CRABGRAB\\Desktop\\DEV\\workspace\\CrabGrab\\bin\\processing\\anim_crab.png");

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// calibration
		matCali = new Mat(3, 3, CvType.CV_32F);
		matSrc = new Mat(1, 1, CvType.CV_32FC2);
		matDest = new Mat(1, 1, CvType.CV_32FC2);

		// retrieve matrix
		try
		{
			// use relative path
			Scanner scanner = new Scanner(new File(
					"C:\\Users\\CRABGRAB\\Desktop\\DEV\\16dlm-tb-209\\TB\\workspace\\CrabGrab\\bin\\matrix.csv"));
			scanner.useDelimiter(";");
			int rows = 0;
			int cols = 0;
			while (scanner.hasNext())
			{
				if (cols < 3)
				{
					matCali.put(rows, cols,
							Double.valueOf(scanner.next()).floatValue());
					cols++;
				} else
				{
					cols = 0;
					rows++;
				}
			}
			scanner.close();
		} catch (FileNotFoundException e)
		{
			System.out.println("File hasn't been found");
			e.printStackTrace();
		}
		System.out.println("Coucou");

		randomSeed(0);

		// peuplage
		for (int i = 0; i < 20; i++)
		{
			flock.addBoid(new Crab(this, width / 2, height / 2, spritesheet));
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
		background(0);

		tracker.setThreshold(3200);
		tracker.track();
		tracker.display();

		frame.setTitle(tracker.getThreshold() + "");

		// convert coordinates
		matSrc.put(0, 0, new float[]{tracker.getPos().x, tracker.getPos().y});
		Core.perspectiveTransform(matSrc, matDest, matCali);

		int targetX =(int) (matDest.get(0, 0)[0] * 1024);
		int targetY = (int) (matDest.get(0, 0)[1] * 768);

		ellipse((int) (matDest.get(0, 0)[0] * 1024),
				(int) (matDest.get(0, 0)[1] * 768), 100, 100);

		flock.run();
	}

	@Override
	public void keyPressed()
	{
		if (key == CODED)
		{
			if (keyCode == UP)
			{
				tracker.captureRef();
				System.out.println("REF");
			}
		}
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}
