
package processing.kinect;

import org.openkinect.processing.Kinect2;

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

		display = parent.createImage(kinect.depthWidth, kinect.depthHeight, PConstants.RGB);

		loc = new PVector(0, 0);
		lerpedLoc = new PVector(0, 0);
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

		float sumX = 0.0f;
		float sumY = 0.0f;
		float count = 0.0f;

		for (int i = 0; i < kinect.depthWidth; i++)
		{
			for (int j = 0; j < kinect.depthHeight; j++)
			{
				int offset = kinect.depthWidth - i - 1 + j * kinect.depthWidth;
				int rawDepth = depth[offset];

				if (rawDepth > 0 && rawDepth < threshold)
				{
					sumX += i;
					sumY += j;
					count++;
				}
			}
		}

		if (count != 0)
		{
			loc = new PVector(sumX / count, sumY / count);
		}

		lerpedLoc.x = PApplet.lerp(lerpedLoc.x, loc.x, 0.3f);
		lerpedLoc.y = PApplet.lerp(lerpedLoc.y, loc.y, 0.3f);
	}

	public void display()
	{
		PImage img = kinect.getDepthImage();

		display.loadPixels();
		for (int i = 0; i < kinect.depthWidth; i++)
		{
			for (int j = 0; j < kinect.depthHeight; j++)
			{
				int offset = (kinect.depthWidth - i - 1) + j * kinect.depthWidth;
				int rawDepth = depth[offset];
		        int pix = i + j*display.width;
		        if (rawDepth > 0 && rawDepth < threshold) {
		          // A red color instead
		          display.pixels[pix] = parent.color(150, 50, 50);
		        } else {
		          display.pixels[pix] = img.pixels[offset];
		        }
			}
		}
		display.updatePixels();

	    // Draw the image
	    parent.image(display, parent.width - kinect.depthWidth, parent.height - kinect.depthHeight);
	}

	public PVector getLerpedPos()
	{
		return lerpedLoc;
	}

	public PVector getPos()
	{
		return loc;
	}

	public int getThreshold()
	{
		return threshold;
	}

	public void setThreshold(int t)
	{
		threshold = t;
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

	private int threshold = 745;
	private PVector loc;
	private PVector lerpedLoc;
	private int depth[];
	private PImage display;

	private Kinect2 kinect;
}
