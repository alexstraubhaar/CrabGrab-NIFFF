
package processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import processing.core.PApplet;
import processing.core.PVector;
import processing.kinect.KinectTracker;
import processing.seek.Vehicle;

/**
 *
 * @author Alexandre Straubhaar
 *
 *         This class generates a small vehicle (triangle or crab) that seeks
 *         the mouse position or the kinect tracking position and stops when it
 *         reaches it.
 *
 */

public class UseSeek extends PApplet
{

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		String[] a = {"MAIN"};
		PApplet.runSketch(a, new UseSeek());
	}

	KinectTracker tracker;
	Vehicle vehicle;
	Mat matDest;
	Mat matSrc;
	Mat matCali;

	public void settings()
	{
		// render use OpenGL 2D

		// fullScreen(P2D);
	}

	@Override
	public void setup()
	{
		size(1024, 768, P2D);
		vehicle = new Vehicle(this, width / 2, height / 2);
		tracker = new KinectTracker(this);

		// load openCV
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
					"C:\\Users\\CRABGRAB\\Desktop\\DEV\\16dlm-tb-209\\TB\\workspace\\CrabGrab\\bin\\processing\\matrix.csv"));
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
	}

	@Override
	public void draw()
	{
		background(100);
		vehicle.display();
		vehicle.update();

		// tracker.setThreshold(2000);
		tracker.track();
		// tracker.display();

		List<PVector> listPositions = tracker.getPositions();
		for (PVector position : listPositions)
		{
			// convert coordinates
			matSrc.put(0, 0,
					new float[]{position.x, position.y});
			Core.perspectiveTransform(matSrc, matDest, matCali);

			int targetX = Math.abs((int) (matDest.get(0, 0)[0] * 1024 - 1024));
			int targetY = (int) (matDest.get(0, 0)[1] * 768);

			vehicle.seek(new PVector(targetX, targetY));
			ellipse((int) (Math.abs(matDest.get(0, 0)[0] * 1024 - 1024)),
					(int) (matDest.get(0, 0)[1] * 768), 5, 5);
		}
	}

	@Override
	public void keyPressed()
	{
		int t = tracker.getThreshold();
		if (key == CODED)
		{
			if (keyCode == UP)
			{
				t += 5;
				tracker.setThreshold(t);
			} else if (keyCode == DOWN)
			{
				t -= 5;
				tracker.setThreshold(t);
			}
		}
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}
