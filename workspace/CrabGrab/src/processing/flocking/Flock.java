
package processing.flocking;

import java.util.ArrayList;
import java.util.Iterator;

public class Flock implements Iterable<GeneralBoid>
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public Flock()
	{
		boids = new ArrayList<GeneralBoid>();
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	public void run()
	{
		for (GeneralBoid b : boids)
		{
			b.run(boids);
		}
	}

	public void addBoid(GeneralBoid b)
	{
		boids.add(b);
	}

	@Override
	public Iterator<GeneralBoid> iterator()
	{
		Iterator<GeneralBoid> it = new Iterator<GeneralBoid>() {

			private int currentIndex = 0;
			private int currentSize = boids.size();

			@Override
			public boolean hasNext()
			{
				return currentIndex < currentSize && boids.get(currentIndex) != null;
			}

			@Override
			public GeneralBoid next()
			{
				return boids.get(currentIndex++);
			}

		};
		return it;
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

	private ArrayList<GeneralBoid> boids; // contains everything
}

