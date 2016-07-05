
package processing;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.flocking.Crab;
import processing.flocking.Flock;
import processing.flocking.GeneralBoid;
import processing.flocking.Human;
import processing.gui.ControlFrame;
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

	// flock components
	Flock flock;
	ArrayList<GeneralBoid> humans;
	ArrayList<GeneralBoid> crabs;
	ArrayList<GeneralBoid> crabsToRemove;

	// number of crabs
	int crabNumber;

	// spritesheet
	PImage spritesheet;

	List<PVector> listPositions;

	int x, y;
	Mat matDest;
	Mat matSrc;
	Mat matCali;

	float minAlea = 0.7f;
	float maxAlea = 1.3f;

	// controls
	ControlFrame controlFrame;

	// beamer size
	Rectangle beamerSize;

	@Override
	public void setup()
	{
		// extended screen / Beamer setup
		// beamerSize = new Rectangle();
		// GraphicsEnvironment ge = GraphicsEnvironment
		// .getLocalGraphicsEnvironment();
		// GraphicsDevice[] gs = ge.getScreenDevices();
		// GraphicsDevice beamer = gs[1];
		// GraphicsConfiguration[] gc = beamer.getConfigurations();
		// beamerSize = gc[0].getBounds();

		// size(1024, 768, P2D);

		// fullscreen
		size(1024, 768, P2D);

		frameRate(30);

		// for non-kinect use
		listPositions = new ArrayList<PVector>();

		tracker = new KinectTracker(this);

		// control frame
		controlFrame = ControlFrame.addControlFrame(this, 600, 500, tracker);

		crabNumber = 100;

		crabs = new ArrayList<GeneralBoid>();
		humans = new ArrayList<GeneralBoid>();
		crabsToRemove = new ArrayList<GeneralBoid>();

		spritesheet = loadImage("img\\anim_crab_orange.png");

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// calibration
		matCali = new Mat(3, 3, CvType.CV_32F);
		matSrc = new Mat(1, 1, CvType.CV_32FC2);
		matDest = new Mat(1, 1, CvType.CV_32FC2);

		// retrieve matrix
		try
		{
			// use relative path
			Scanner scanner = new Scanner(new File("matrix.csv"));
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
		for (int i = 0; i < crabNumber; i++)
		{
			crabs.add(new Crab(this, width / 2, height / 2,
					random(minAlea, maxAlea), spritesheet));
		}
	}

	@Override
	public void draw()
	{
		// main app on second screen
		// frame.setLocation(beamerSize.x, beamerSize.y);
		// frame.setAlwaysOnTop(true);

		background(0);

		tracker.setThreshold(3200);
		tracker.track();
		tracker.display();

		frame.setTitle(frameRate + " - " + tracker.getThreshold() + "");

		// since the detections stutters, we need to clean the list everytime we
		// detect something
		// or we would have too many humans
		flock = new Flock();

		humans.clear();
		crabsToRemove.clear();

		// we use mouse position for debug
		// List<PVector> listPositions = tracker.getPositions();

		if (!listPositions.isEmpty())
		{
			for (PVector position : listPositions)
			{
				// convert coordinates
				matSrc.put(0, 0, new float[]{position.x, position.y});

				Core.perspectiveTransform(matSrc, matDest, matCali);

				int targetX = (int) (matDest.get(0, 0)[0] * 1024);
				int targetY = (int) (matDest.get(0, 0)[1] * 768);

				humans.add(new Human(this, position.x, position.y));
				// humans.add(new Human(this, targetX, targetY));

				// ellipse(targetX, targetY, 5, 5);
				ellipse(position.x, position.y, 5, 5);
			}
		}

		// is there enough crabs ?
		while (crabs.size() < crabNumber)
		{
			crabs.add(crabs.size() - 1, new Crab(this, random(0, width),
					random(0, height), random(minAlea, maxAlea), spritesheet));
		}

		for (GeneralBoid human : humans)
		{
			flock.addBoid(human);
		}
		for (GeneralBoid crab : crabs)
		{
			// check if the crab is out of bounds
			if (((Crab) crab).getOutOfBounds())
			{
				crabsToRemove.add(crab);
			} else
			{
				flock.addBoid(crab);
			}
		}

		// deleting the out of bounds crabs
		for (GeneralBoid crabToRemove : crabsToRemove)
		{
			crabs.remove(crabToRemove);
		}

		for (GeneralBoid crab : crabs)
		{
			for (GeneralBoid human : humans)
			{
				PVector predBoid = human.getLocation();
				((Crab) crab).repelForce(predBoid, 42);
			}

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
		listPositions.add(new PVector(mouseX, mouseY));
	}

	@Override
	public void init()
	{
		frame.removeNotify();
		frame.setUndecorated(true);
		super.init();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}
