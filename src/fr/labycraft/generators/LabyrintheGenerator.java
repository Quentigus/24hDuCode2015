package fr.labycraft.generators;

import com.ardor3d.math.MathUtils;
import com.ardorcraft.data.Pos;
import com.ardorcraft.generators.DataGenerator;
import com.ardorcraft.util.ImprovedNoise;
import com.ardorcraft.world.BlockWorld;
import com.ardorcraft.world.WorldModifier;
import fr.labycraft.Modele.Labyrinthe;
import fr.labycraft.Modele.MazeGenerator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 *
 * @author Bastien Andru <bastien.andru@gmail.com>
 */
public class LabyrintheGenerator implements DataGenerator, Observer {
    
    public LabyrintheGenerator(){
        generateLabyrinthe();
    }
    
    private static final int waterHeight = 0;
    
    private int[][] tab;
    private int startx;
    private int starty;
    
    @Override
    public void update(Observable o, Object arg) {
        if ((boolean)arg){
            //generateLabyrinthe();
            for(int l = 12; l<15;l++){
                tab[startx][starty+l] = 5;
                tab[startx+24][starty+l] = 5;
                tab[startx+l][starty] = 5;
                tab[startx+l][starty+24] = 5;
            }
        }/*else{
            generateLabyrinthe();
        }*/
    }
    
    @Override
    public void generateChunk(int xStart, int zStart, int xEnd, int zEnd, int spacing, int height, WorldModifier proxy) {
        for (int x = xStart;x < xEnd;x++) {
            for (int y = zStart;y < zEnd;y++) {
                
                if (x >= 0 && x < tab.length
                        &&y >= 0 && y < tab[0].length) {
                    int z = 0;
                    Random rand1 = new Random();
                    if (tab[x][y] == 1) {
                        for(int k = 1;k<rand1.nextInt(6)+7;k++){
                            proxy.setBlock(x, k, y, 1);
                        }
                    }
                    if (tab[x][y] == 4) {
                        for(int k = 1;k<11;k++){
                            proxy.setBlock(x, k, y, 1);
                        }
                    }
                    if (tab[x][y] == 5) {
                        for(int k = 1;k<11;k++){
                            proxy.setBlock(x, k, y, 2);
                        }
                    }
                    if (tab[x][y] == 2 || tab[x][y]== 0) {
                        proxy.setBlock(x, 1, y, 2);
                    }
                    if (tab[x][y] == 3) {
                        
                        //for (int k = 0; k < 5; k++) {
                        //   proxy.setBlock(x, k, y, 17);
                        //}
                        Pos p = new Pos(x, 0, y);
                        Random rand = new Random();
                        addTree(proxy, p, 10, rand);
                    }
                }
            }
        }
    }
    
    public void generateLabyrinthe(){
        MazeGenerator maze = new Labyrinthe(26, 26);
        maze.carveSafeZone(12, 12);
        maze.generate();
        //maze.printBoolean();
        tab = maze.getMazeBool();
        safeZone();
    }
    
    private void safeZone(){
        startx = Math.round((tab.length - 25)/ 2);
        starty = Math.round((tab[0].length - 25)/ 2);
        for (int i = startx; i<(startx+25);i++){
            for (int j = starty;j<(starty+25);j++){
                if (i == startx || j == starty || i == startx+24 || j == starty+24){
                    tab[i][j] = 4;
                }else{
                    tab[i][j] = 2;
                }
                tab[startx+12][starty+13] = 3;
            }
        }
        for(int l = 12; l<15;l++){
            tab[startx][starty+l] = 0;
            tab[startx+24][starty+l] = 0;
            tab[startx+l][starty] = 0;
            tab[startx+l][starty+24] = 0;
        }
        
    }
    
    private void addTree(final WorldModifier blockScene, final Pos pos, final int treeHeight, final Random rand) {
        for (int y = 0; y < treeHeight; y++) {
            blockScene.setBlock(pos.x, pos.y + y, pos.z, 17);
        }
        
        for (int x = 0; x < treeHeight; x++) {
            for (int z = 0; z < treeHeight; z++) {
                for (int y = 0; y < treeHeight; y++) {
                    final int xx = x - (treeHeight - 1) / 2;
                    final int yy = y - (treeHeight - 1) / 2;
                    final int zz = z - (treeHeight - 1) / 2;
                    if (xx == 0 && zz == 0 && yy <= 0) {
                        continue;
                    }
                    final double test = MathUtils.sqrt((double) xx * xx + yy * yy + zz * zz);
                    if (test < (treeHeight - 1.0) / 2.0) {
                        if (rand.nextDouble() < 0.8) {
                            blockScene.setBlock(pos.x + xx, pos.y + yy + treeHeight - 1, pos.z + zz, 18);
                        }
                    }
                }
            }
        }
    }
    
    private void addTree(final BlockWorld blockScene, final Pos pos, final int treeHeight, final Random rand) {
        for (int y = 0; y < treeHeight; y++) {
            blockScene.setBlock(pos.x, pos.y + y, pos.z, 17);
        }
        
        for (int x = 0; x < treeHeight; x++) {
            for (int z = 0; z < treeHeight; z++) {
                for (int y = 0; y < treeHeight; y++) {
                    final int xx = x - (treeHeight - 1) / 2;
                    final int yy = y - (treeHeight - 1) / 2;
                    final int zz = z - (treeHeight - 1) / 2;
                    if (xx == 0 && zz == 0 && yy <= 0) {
                        continue;
                    }
                    final double test = MathUtils.sqrt((double) xx * xx + yy * yy + zz * zz);
                    if (test < (treeHeight - 1.0) / 2.0) {
                        if (rand.nextDouble() < 0.8) {
                            blockScene.setBlock(pos.x + xx, pos.y + yy + treeHeight - 1, pos.z + zz, 18);
                        }
                    }
                }
            }
        }
    }
    
    public void generatePorte(BlockWorld blockWorld){
        //generateLabyrinthe();
            for(int l = 12; l<15;l++){
                for(int h = 0;h<11;h++){
                    blockWorld.setBlock(startx, h, starty+l, 3);
                    blockWorld.setBlock(startx+24, h, starty+l, 3);
                    blockWorld.setBlock(startx+l, h, starty, 3);
                    blockWorld.setBlock(startx+l, h, starty+24, 3);
                }
            }
             
    }
    
    public void generateLabyrinthe(BlockWorld blockWorld) {
         for (int x = 0;x < tab.length;x++) {
            for (int y = 0;y < tab[0].length;y++) {
                for(int k = 1;k<14;k++){
                    blockWorld.setBlock(x, k, y, 0);
                }
            }
         }
         
        for (int x = 0;x < tab.length;x++) {
            for (int y = 0;y < tab[0].length;y++) {
                
                if (x >= 0 && x < tab.length && y >= 0 && y < tab[0].length) {
                    int z = 0;
                    Random rand1 = new Random();
                    if (tab[x][y] == 1) {
                        for(int k = 1;k<rand1.nextInt(6)+7;k++){
                            blockWorld.setBlock(x, k, y, 1);
                        }
                    }
                    if (tab[x][y] == 4) {
                        for(int k = 1;k<11;k++){
                            blockWorld.setBlock(x, k, y, 1);
                        }
                    }
                    if (tab[x][y] == 5) {
                        for(int k = 1;k<11;k++){
                            blockWorld.setBlock(x, k, y, 2);
                        }
                    }
                    if (tab[x][y] == 2 || tab[x][y]== 0) {
                        blockWorld.setBlock(x, 1, y, 2);
                    }
                    if (tab[x][y] == 3) {
                        
                        Pos p = new Pos(x, 0, y);
                        Random rand = new Random();
                        addTree(blockWorld, p, 10, rand);
                    }
                }
            }
        }
    }  
    

}
