
package processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import controlP5.ControlEvent;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.flocking.Crab;
import processing.flocking.Flock;
import processing.flocking.GeneralBoid;
import processing.flocking.Human;
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
	ArrayList<GeneralBoid> humans;
	ArrayList<GeneralBoid> crabs;
	List<PVector> listPositions;

	int x, y;
	boolean seekMouse = false;
	Mat matDest;
	Mat matSrc;
	Mat matCali;

	// controls
	ControlWindow controlWindow;

	@Override
	public void setup()
	{
		size(1024, 768, P2D);

		// control window
		controlWindow = new ControlWindow(this);

		tracker = new KinectTracker(this);
		crabs = new ArrayList<GeneralBoid>();
		PImage spritesheet = loadImage(
				// "C:\\Users\\User\\Documents\\He-Arc\\3\\NIFFF\\TB\\workspace\\CrabGrab\\anim_crab.png");
				"C:\\Users\\CRABGRAB\\Desktop\\DEV\\16dlm-tb-209\\TB\\workspace\\CrabGrab\\bin\\anim_crab_orange.png");

		listPositions = new ArrayList<PVector>();

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
					// "C:\\Users\\User\\Documents\\He-Arc\\3\\NIFFF\\TB\\workspace\\CrabGrab\\matrix.csv"));
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

		randomSeed(0);

		// peuplage
		for (int i = 0; i < 50; i++)
		{
			crabs.add(new Crab(this, width / 2, height / 2, random(0.9f, 1.1f),
					spritesheet));
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

		// since the detections stutters, we need to clean the list everytime we
		// detect something
		// or we would have too many humans
		flock = new Flock();
		humans = new ArrayList<GeneralBoid>();

		// we use mouse position for debug
		List<PVector> listPositions = tracker.getPositions();

		if (!listPositions.isEmpty())
		{
			for (PVector position : listPositions)
			{
				// convert coordinates
				matSrc.put(0, 0, new float[]{position.x, position.y});

				Core.perspectiveTransform(matSrc, matDest, matCali);

				int targetX = (int) (matDest.get(0, 0)[0] * 1024);
				int targetY = (int) (matDest.get(0, 0)[1] * 768);

				// humans.add(new Human(this, position.x, position.y));
				humans.add(new Human(this, position.x, position.y));

				ellipse(targetX, targetY, 20, 20);
			}
		}

		for (GeneralBoid human : humans)
		{
			flock.addBoid(human);
		}
		for (GeneralBoid crab : crabs)
		{
			flock.addBoid(crab);
		}

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

	@Override
	public void mousePressed()
	{
		// listPositions.add(new PVector(mouseX, mouseY));
	}

	// event call for GUI
	public void controlEvent(ControlEvent event)
	{
		switch (event.getController().getName())
		{
			// crab
			case "crab_wsep" :
				Crab.setWSep(event.getController().getValue());
				break;
			case "crab_wali" :
				Crab.setWAli(event.getController().getValue());
				break;
			case "crab_wcoh" :
				Crab.setWCoh(event.getController().getValue());
				break;
			case "crab_size" :
				Crab.setSize((int) event.getController().getValue());
				break;
			case "crab_neighbor" :
				Crab.setNeighbor(event.getController().getValue());
				break;
			case "crab_sep" :
				Crab.setSepLimit(event.getController().getValue());
				break;
			case "crab_speed" :
				Crab.setMaxSpeed(event.getController().getValue());
				break;

			// crab vs human
			case "crab_humansep" :
				Crab.setHumanSepLimit(event.getController().getValue());
				break;

			// human
			case "human_wsep" :
				Human.setWSep(event.getController().getValue());
				break;
			case "human_wali" :
				Human.setWAli(event.getController().getValue());
				break;
			case "human_wcoh" :
				Human.setWCoh(event.getController().getValue());
				break;
		}
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}
