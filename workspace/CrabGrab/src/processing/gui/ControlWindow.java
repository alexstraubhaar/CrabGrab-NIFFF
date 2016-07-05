
package processing.gui;

import controlP5.ControlP5;
import processing.core.PApplet;

public class ControlWindow
{
	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public ControlWindow(PApplet p)
	{
		parent = p;
		control = new ControlP5(p);

		// sliders
		// Crab
		control.addSlider("crab_wsep").setPosition(40, 40).setSize(200, 20)
				.setRange(0.0f, 5.0f).setValue(0.6f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		control.addSlider("crab_wcoh").setPosition(40, 70).setSize(200, 20)
				.setRange(0.0f, 5.0f).setValue(0.65f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		control.addSlider("crab_wali").setPosition(40, 100).setSize(200, 20)
				.setRange(0.0f, 5.0f).setValue(0.7f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		control.addSlider("crab_size").setPosition(40, 130).setSize(200, 20)
				.setRange(25, 75).setValue(48)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		control.addSlider("crab_neighbor").setPosition(40, 160).setSize(200, 20)
				.setRange(0f, 1024f).setValue(50f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		control.addSlider("crab_sep").setPosition(40, 190).setSize(200, 20)
				.setRange(50f, 1024f).setValue(69f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		control.addSlider("crab_speed").setPosition(40, 220).setSize(200, 20)
				.setRange(2.0f, 10.0f).setValue(5.2f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		// Crab vs Human
		control.addSlider("crab_humansep").setPosition(310, 70).setSize(200, 20)
				.setRange(50f, 1024f).setValue(605f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		control.addSlider("crab_humanneigh").setPosition(310, 100)
				.setSize(200, 20).setRange(50f, 1024f).setValue(50f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));

		// Human
		control.addSlider("human_wcoh").setPosition(310, 40).setSize(200, 20)
				.setRange(0.0f, 25.0f).setValue(13.0f)
				.setColorCaptionLabel(parent.color(20, 20, 20))
				.setColorLabel(parent.color(255, 255, 255));
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

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

	private ControlP5 control;
}
