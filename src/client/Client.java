package client;

import components.Game;
import components.Scene;

import javax.swing.*;
import java.awt.*;

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

        // Create and set a layout with 3 rows
        // 1st is the status panel
        // 2nd is the picture panel
        // 3rd is the button and text panel
        GridLayout layout = new GridLayout(3, 1);
        frame.setLayout(layout);
        JPanel statusPanel = new JPanel();
        frame.add(statusPanel);
        JPanel mainPanel = new JPanel();
        frame.add(mainPanel);
        JPanel interactionPanel = new JPanel();
        GridLayout buttonLayout = new GridLayout(2, 2);
        interactionPanel.setLayout(buttonLayout);
        frame.add(interactionPanel);

        // Create buttons
        JButton choice1 = new JButton("Choice 1");
        JButton choice2 = new JButton("Choice 2");
        JButton choice3 = new JButton("Choice 3");
        JButton choice4 = new JButton("Choice 4");

        // Read Start scene

        // Set curr scene

        // Add buttons and stuff
        interactionPanel.add(choice1);
        interactionPanel.add(choice2);
        interactionPanel.add(choice3);
        interactionPanel.add(choice4);

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
