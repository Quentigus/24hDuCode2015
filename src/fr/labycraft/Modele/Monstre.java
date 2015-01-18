/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.labycraft.Modele;

import com.ardorcraft.world.BlockWorld;



/**
 *
 * @author Byakuuu
 */
public class Monstre {
    
private int posx;
private int posy;
private int posz;
private BlockWorld blockWorld;

    public Monstre(int posx, int posy,int posz, BlockWorld blockWorld){
        this.blockWorld = blockWorld;
        this.posx = posx;
        this.posy = posy;
        this.posz = posz;
        this.blockWorld.setBlock(posx, posy, posz, 86);
    }
    
    public void supprimerMonstre(){
        blockWorld.setBlock(posx, posy, posz, 0);
    }
    
}
