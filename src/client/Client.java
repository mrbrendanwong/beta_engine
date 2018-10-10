package client;

import components.Game;
import components.Scene;
import lib.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client implements ActionListener {
    private int NUM_BUTTONS = 4;

    private Game game;
    private JFrame frame;
    private JPanel statusPanel;
    private JPanel mainPanel;
    private JPanel interactionPanel;
    private Scene currScene;
    private List<JButton> buttons = new ArrayList<>();

    public Client(Game gameObj) {
        game = gameObj;

        // Create the JFrame
        frame = new JFrame(game.title);
        frame.pack();
        frame.setSize(1280, 720);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set a layout with 3 rows
        // 1st is the status panel
        // 2nd is the picture panel
        // 3rd is the button and text panel
        GridLayout layout = new GridLayout(3, 1);
        frame.setLayout(layout);

        statusPanel = new JPanel();
        mainPanel = new JPanel();
        interactionPanel = new JPanel();
        GridLayout buttonLayout = new GridLayout(2, 2);
        interactionPanel.setLayout(buttonLayout);

        frame.add(statusPanel);
        frame.add(mainPanel);
        frame.add(interactionPanel);

        // Create buttons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            buttons.add(new JButton());
            buttons.get(i).setActionCommand(String.format("%d", i));
            buttons.get(i).addActionListener(this);
        }

        // Set start scene to the current scene
        currScene = game.startScene;

        // Add buttons and stuff
        for (int i = 0; i < currScene.choices.size(); i++) {
            buttons.get(i).setText(currScene.choices.get(i).text);
            interactionPanel.add(buttons.get(i));
        }
    }

    // Temp
    public void launchFrame(){
        frame.setVisible(true);
    }

    // Updates current elements of game on the frame
    private void updateFrame(int buttonNum) {
        String nextScene = currScene.choices.get(buttonNum).next;
        for (Node node : game.storyScenes) {
            Scene storyScene = (Scene) node;
            if (Objects.equals(storyScene.name, nextScene)) {
                currScene = storyScene;
            }
        }
        updateButtons();
    }

    private void updateButtons() {
        interactionPanel.removeAll();
        for (int i = 0; i < currScene.choices.size(); i++) {
            buttons.get(i).setText(currScene.choices.get(i).text);
            interactionPanel.add(buttons.get(i));
        }
        interactionPanel.revalidate();
        interactionPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateFrame(Integer.parseInt(e.getActionCommand()));
    }
}
