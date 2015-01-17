package fr.labycraft.generators;

import com.ardorcraft.generators.DataGenerator;
import com.ardorcraft.world.WorldModifier;

/**
 *
 * @author Bastien Andru <bastien.andru@gmail.com>
 */
public class SafeZoneGenerator implements DataGenerator{

	/**
	 * Default constructor of <code>SafeZoneGenerator</code>.
	 */
	public SafeZoneGenerator() {
		
	}

	@Override
	public void generateChunk(int xStart, int zStart, int xEnd, int zEnd, int spacing, int height, WorldModifier proxy) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}