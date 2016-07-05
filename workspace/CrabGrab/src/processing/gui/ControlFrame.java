
package processing.gui;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.flocking.Crab;
import processing.flocking.Human;
import processing.kinect.KinectTracker;

/**
 *
 * @author Alexandre Straubhaar
 *
 *         This class is used to put the GUI in another window, as suggested by
 *         the official ControlP5 documentation
 *         http://www.sojamo.de/libraries/controlP5/examples/extra/
 *         ControlP5frame/ControlP5frame.pde
 *
 */
public class ControlFrame extends PApplet
{
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public ControlFrame(PApplet p, int width, int height, KinectTracker tracker)
	{
		parent = p;
		w = width;
		h = height;

		this.tracker = tracker;
	}

	private ControlFrame()
	{
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	@Override
	public void draw()
	{
		// Draw the image
		image(tracker.getDisplay(), 40, 280, 200, 140);
	}

	@Override
	public void setup()
	{
		size(w, h);
		background(100);
		frameRate(25);

		control = new ControlP5(this);

		control.addSlider("crab_wsep").setPosition(40, 40).setSize(200, 20)
				.setRange(0.0f, 5.0f).setValue(2.0f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		control.addSlider("crab_wcoh").setPosition(40, 70).setSize(200, 20)
				.setRange(0.0f, 5.0f).setValue(0.65f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		control.addSlider("crab_wali").setPosition(40, 100).setSize(200, 20)
				.setRange(0.0f, 5.0f).setValue(1.7f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		control.addSlider("crab_size").setPosition(40, 130).setSize(200, 20)
				.setRange(25, 75).setValue(41)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		control.addSlider("crab_neighbor").setPosition(40, 160).setSize(200, 20)
				.setRange(0f, 1024f).setValue(450f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		control.addSlider("crab_sep").setPosition(40, 190).setSize(200, 20)
				.setRange(50f, 1024f).setValue(230f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		control.addSlider("crab_speed").setPosition(40, 220).setSize(200, 20)
				.setRange(2.0f, 10.0f).setValue(6f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		// Crab vs Human
		control.addSlider("crab_humansep").setPosition(310, 70).setSize(200, 20)
				.setRange(50f, 1024f).setValue(605f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));

		control.addSlider("crab_humanneigh").setPosition(310, 100)
				.setSize(200, 20).setRange(50f, 1024f).setValue(50f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		// Human
		control.addSlider("human_wcoh").setPosition(310, 40).setSize(200, 20)
				.setRange(0.0f, 25.0f).setValue(13.0f)
				.setColorCaptionLabel(color(20, 20, 20))
				.setColorLabel(color(255, 255, 255));
	}

	// event call for GUI sliders
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

			case "crab_humanneigh" :
				Crab.setHumanNeigh(event.getController().getValue());
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

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

	public static ControlFrame addControlFrame(PApplet p, int width, int height,
			KinectTracker tracker)
	{
		Frame f = new Frame("Controls");
		ControlFrame newControlFrame = new ControlFrame(p, width, height,
				tracker);

		// frame
		f.add(newControlFrame);
		newControlFrame.init();
		f.setTitle("Controls");
		f.setSize(width, height);
		f.setLocation(200, 200);
		f.setResizable(false);
		f.setVisible(true);

		f.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				// for output file differenciation
				System.out.println("\n");
				System.out.println("================================== FINI ======================================");
				System.out.println("\n");
				p.exit();
			}
		});

		return newControlFrame;
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

	// parent class
	private PApplet parent;

	private ControlP5 control;

	private KinectTracker tracker;

	// size
	private int w;
	private int h;
}
