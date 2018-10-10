package client;

import components.Game;
import components.Scene;

import javax.swing.*;

public class Client {
    private Game game;
    private JFrame frame;
    public Scene currScene;

    public Client(Game gameObj) {
        game = gameObj;

        // Create the JFrame
        frame = new JFrame(game.title);
        frame.setSize(1280, 720);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create buttons

        // Read Start scene

        // Set curr scene

        // Add buttons and stuff

        // On click actions, update references
    }

    // Temp
    public void launchFrame(){
        frame.setVisible(true);
    }

    // Updates current elements of game on the frame
    private void updateFrame() {

    }

}
