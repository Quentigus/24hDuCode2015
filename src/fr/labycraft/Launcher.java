package fr.labycraft;

import com.ardorcraft.base.ArdorBaseApplication;
import com.ardorcraft.examples.simple.SimpleGame;

public class Launcher extends ArdorBaseApplication {

    public Launcher() {
        super(new SimpleGame());
    }

    public static void main(final String[] args) {
        final ArdorBaseApplication example = new Launcher();
        new Thread(example, "MainArdorThread").start();
    }
}
