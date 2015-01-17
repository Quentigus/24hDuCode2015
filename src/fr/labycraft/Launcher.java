package fr.labycraft;

import com.ardorcraft.base.ArdorBaseApplication;

public class Launcher extends ArdorBaseApplication {

    public Launcher() {
        super(new Game());
    }

    public static void main(final String[] args) {
        final ArdorBaseApplication example = new Launcher();
        new Thread(example, "MainArdorThread").start();
    }
}
