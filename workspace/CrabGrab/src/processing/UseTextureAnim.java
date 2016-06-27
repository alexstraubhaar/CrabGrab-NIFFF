
package processing;

import processing.core.PApplet;
import processing.textureanim.TextureAnim;

public class UseTextureAnim extends PApplet
{
	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public static void main(String[] args)
	{
		String[] a = {"MAIN"};
		PApplet.runSketch(a, new UseTextureAnim());
	}

	TextureAnim anim;

	@Override
	public void setup()
	{
		size(1280, 720, P2D);
		anim = new TextureAnim(this);
	}

	public void settings()
	{

	}

	@Override
	public void draw()
	{
		anim.draw();
	}
	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

}
