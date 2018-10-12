package client;

import components.Choice;
import components.Game;
import components.Scene;
import lib.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Client implements ActionListener {
    private int NUM_BUTTONS = 4;

    private Game game;
    private Scene currScene;
    private int currTextPointer;
    private int[] choiceToButtonIndex = new int[NUM_BUTTONS];

    private JFrame frame;
    private JPanel statusPanel;
    private JPanel mainPanel;
    private JPanel interactionPanel;
    private JPanel textPanel;
    private JButton nextButton;
    private List<JButton> choiceButtons = new ArrayList<>();

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

        // Create text and next button
        textPanel = new JPanel();
        nextButton = new JButton("Next");
        nextButton.setActionCommand(String.format("%d", -1));
        nextButton.addActionListener(this);

        // Create choiceButtons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            choiceButtons.add(new JButton());
            choiceButtons.get(i).setActionCommand(String.format("%d", i));
            choiceButtons.get(i).addActionListener(this);
        }

        // Set start scene to the current scene
        currScene = game.startScene;

        // Add text and choiceButtons and stuff
        updateFrame(-1);
    }

    // Temp
    public void launchFrame(){
        frame.setVisible(true);
    }

    // Updates current elements of game on the frame
    private void updateFrame(int choiceNum) {
        // Current scene
        String oldScene = currScene.name;
        // Get next scene
        Scene scene;
        // Only get next scene if a choice was made
        if (choiceNum != -1) {
            // set new stat
            int sceneChoiceIndex = choiceToButtonIndex[choiceNum];
            Choice c = currScene.choices.get(sceneChoiceIndex);
            if (c.statString != null) {
                c.setStat();
            }

            // reset text pointer
            currTextPointer = 0;
            String nextScene = currScene.choices.get(sceneChoiceIndex).next;
            for (Node node : game.storyScenes) {
                scene = (Scene) node;
                if (scene.name.equals(nextScene)) {
                    currScene = scene;
                }
            }
            for (Node node : game.deathScenes) {
                scene = (Scene) node;
                if (scene.name.equals(nextScene)) {
                    currScene = scene;
                }
            }
            for (Node node : game.endScenes) {
                scene = (Scene) node;
                if (scene.name.equals(nextScene)) {
                    currScene = scene;
                }
            }
            if (oldScene.equals(currScene.name)) {
                System.out.println("Infinite scene loop");
                // TODO Probably show something?
                System.exit(1);
            }
        }

        // if there's still text to display, then display it, if not then display the choiceButtons
        if (currTextPointer < currScene.texts.size()) {
            updateText();
        } else {
            updateButtons();
        }
    }

    private void updateText() {
        interactionPanel.removeAll();
        textPanel.removeAll();
        textPanel.add(new JLabel(currScene.texts.get(currTextPointer)));
        interactionPanel.add(textPanel);
        interactionPanel.add(nextButton);
        interactionPanel.revalidate();
        interactionPanel.repaint();
        currTextPointer++;
    }

    private void updateButtons() {
        interactionPanel.removeAll();
        // TODO: if there are no more buttons, then quit the application
        if (currScene.choices.size() == 0) {
            System.exit(0);
        }
        int buttonIndex = 0;
        for (int i = 0; i < currScene.choices.size(); i++) {
            Choice c = currScene.choices.get(i);
            if (c.conditionalString == null || c.evalConditional()) {
                choiceButtons.get(buttonIndex).setText(c.text);
                interactionPanel.add(choiceButtons.get(buttonIndex));
                choiceToButtonIndex[buttonIndex] = i;
                buttonIndex++;
            }
            if (buttonIndex == NUM_BUTTONS) {
                break;
            }
        }
        interactionPanel.revalidate();
        interactionPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateFrame(Integer.parseInt(e.getActionCommand()));
    }
}
