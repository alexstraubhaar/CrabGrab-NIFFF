
package processing.kinect;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;
import org.openkinect.processing.Kinect2;

import gab.opencv.OpenCV;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class KinectTracker
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public KinectTracker(PApplet p)
	{
		parent = p;
		kinect = new Kinect2(parent);
		// activation
		kinect.initDepth();
		kinect.initDevice();

		display = parent.createImage(kinect.depthWidth, kinect.depthHeight,
				PConstants.RGB);

		listLocation = new ArrayList<>();
		lerpedLoc = new PVector(0, 0);
		limitRange = 2500;
		mask = parent.loadImage("Mask.png");
	}
	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void track()
	{
		depth = kinect.getRawDepth();
		if (depth == null)
		{
			return;
		}

		PImage pImage = kinect.getDepthImage();
		if (thresholdImage == null)
		{
			thresholdImage = new Mat(pImage.width, pImage.height,
					CvType.CV_32S);
		}
		for (int i = 0; i < pImage.width; i++)
		{
			for (int j = 0; j < pImage.height; j++)
			{

				int offset = i + j * kinect.depthWidth;

				// Subtraction
				int rawDepth = depth[offset]; // - refDepth[offset];

				if (rawDepth > limitRange && rawDepth < threshold
						&& mask.get(i, j) == parent.color(255, 255, 255))
				{
					thresholdImage.put(j, i, new int[]{pImage.get(i, j)});
				} else
				{
					thresholdImage.put(j, i, new int[]{0});
				}
			}
		}

		contours.clear();
		Imgproc.findContours(thresholdImage, contours, new Mat(),
				Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		if (contours.size() >= 1)
		{
			listLocation.clear();
			for (MatOfPoint blob : contours)
			{
				if (blob.size().height > 20)
				{
					PVector center = new PVector(0, 0);
					double x = 0;
					double y = 0;
					for (int i = 0; i < blob.size().height; i++)
					{
						x += blob.get(i, 0)[0];
						y += blob.get(i, 0)[1];
					}
					center.set((int) (x / blob.size().height),
							(int) (y / blob.size().height));
					listLocation.add(center);
				}
			}
		}

		/*
		 *
		 * float sumX = 0.0f; float sumY = 0.0f; float count = 0.0f;
		 *
		 * for (int i = 0; i < kinect.depthWidth; i++) { for (int j = 0; j <
		 * kinect.depthHeight; j++) { int offset = i + j * kinect.depthWidth;
		 *
		 * if (mask.pixels[offset] == parent.color(255, 255, 255)) { if
		 * (refDepth == null) { captureRef(); }
		 *
		 * // Subtraction int rawDepth = depth[offset]; // - refDepth[offset];
		 *
		 * if (rawDepth > limitRange && rawDepth < threshold) { sumX += i; sumY
		 * += j; count++; } } } }
		 *
		 * if (count > 100) { loc = new PVector(sumX / count, sumY / count); }
		 */

		// lerpedLoc.x = PApplet.lerp(lerpedLoc.x, getPos().x, 0.3f);
		// lerpedLoc.y = PApplet.lerp(lerpedLoc.y, getPos().y, 0.3f);
	}

	public void display()
	{
		PImage img = kinect.getDepthImage();
		// img.save("Ref.png");

		display.loadPixels();

		for (int i = 0; i < kinect.depthWidth; i++)
		{
			for (int j = 0; j < kinect.depthHeight; j++)
			{

				int pix = i + j * display.width;
				if (mask.pixels[pix] == parent.color(255, 255, 255))
				{
					int offset = kinect.depthWidth * j + i;// (kinect.depthWidth
															// - i - 1)+ j *
															// kinect.depthWidth;

					// Subtraction
					// int rawDepth = Math.abs(depth[offset] -
					// refDepth[offset]);
					int rawDepth = depth[offset];

					if (rawDepth > limitRange && rawDepth < threshold)
					{ // A red color instead
						display.pixels[pix] = parent.color(50, 150, 50);
					} else
					{
						display.pixels[pix] = img.pixels[pix];
					}
				} else
				{
					display.pixels[pix] = parent.color(150, 50, 50);
				}
			}
		}

		display.updatePixels();

		// Draw the image
		parent.image(display, parent.width - kinect.depthWidth,
				parent.height - kinect.depthHeight);
	}

	public PVector getLerpedPos()
	{
		return lerpedLoc;
	}

	public List<PVector> getPositions()
	{
		return listLocation;
	}

	public int getThreshold()
	{
		return threshold;
	}

	public void setThreshold(int t)
	{
		threshold = t;
	}

	public void captureRef()
	{
		refDepth = kinect.getRawDepth();
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
	private Mat thresholdImage = null;
	private List<MatOfPoint> contours = new ArrayList<>();

	private int threshold = 745;
	private List<PVector> listLocation;
	private PVector lerpedLoc;
	private int depth[];
	private PImage display;
	PImage mask;

	private int limitRange;
	private int[] refDepth;

	OpenCV opencv;

	private Kinect2 kinect;
}
