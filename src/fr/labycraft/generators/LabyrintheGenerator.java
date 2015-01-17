package fr.labycraft.generators;

import com.ardorcraft.generators.DataGenerator;
import com.ardorcraft.world.WorldModifier;

/**
 *
 * @author Bastien Andru <bastien.andru@gmail.com>
 */
public class LabyrintheGenerator implements DataGenerator{
	
	

	/**
	 * Default constructor of <code>FlatGenerator</code>.
	 */
	public LabyrintheGenerator() {
		
	}

	@Override
	public void generateChunk(int xStart, int zStart, int xEnd, int zEnd, int spacing, int height, WorldModifier proxy) {
		proxy.setBlock(8, 8, 1, 2);
		
	}
}