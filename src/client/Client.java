package client;

import components.Choice;
import components.Game;
import components.Picture;
import components.Scene;
import lib.Node;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client implements ActionListener {
    private int NUM_BUTTONS = 4;

    private Game game;
    private Scene currScene;
    private int currTextPointer;
    private int[] buttonToChoiceIndex = new int[NUM_BUTTONS];

    private JFrame frame;
    private JPanel statusPanel;
    private JPanel mainPanel;
    private JPanel interactionPanel;
    private JPanel textPanel;
    private JButton nextButton;
    private JLabel statsLabel;
    private JLabel timer; // TODO
    private List<JButton> choiceButtons = new ArrayList<>();

    // CurrBGM
    private String currBgm;
    private AudioInputStream bgmStream;
    private Clip bgmClip;

    // CurrSound
    private AudioInputStream soundStream;
    private Clip soundClip;

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

        statusPanel = new JPanel();
        mainPanel = new JPanel();
        interactionPanel = new JPanel();

        // Set status panel
        statusPanel.setLayout(new BorderLayout());

        if (Game.stringStats.size() != 0 || Game.numberStats.size() != 0) {
            statsLabel = new JLabel("", SwingConstants.CENTER);
            statusPanel.add(statsLabel, BorderLayout.CENTER);
        }
        // TODO if there is a timer, initialize timerLabel
        timer = new JLabel("", SwingConstants.CENTER);
        statusPanel.add(timer, BorderLayout.SOUTH);
        timer.setText("Time remaining: 30");

        // Set main panel
        mainPanel.setLayout(new OverlayLayout(mainPanel));

        // Set interaction panel
        GridLayout buttonLayout = new GridLayout(2, 2);
        interactionPanel.setLayout(buttonLayout);

        // Set status and interaction panel sizes
        statusPanel.setMinimumSize(new Dimension(0, 50));
        interactionPanel.setMinimumSize(new Dimension(0, 200));

        statusPanel.setPreferredSize(statusPanel.getMinimumSize());
        interactionPanel.setPreferredSize(interactionPanel.getMinimumSize());

        frame.add(statusPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(interactionPanel, BorderLayout.SOUTH);

        interactionPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        if (timer != null || statsLabel != null) {
            statusPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        }

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

        // Add starting pictures
        updatePictures();

        // Add starting music
        if (currScene.bgmFile != null) playAudio(currScene.bgmFile, true);
        if (currScene.soundFile != null) playAudio(currScene.soundFile, false);


        // Add text and choiceButtons and stuff
        updateFrame(-1);
        updateStats();
    }

    public void launchFrame(){
        frame.setVisible(true);
    }

    // Updates current elements of game on the frame
    private void updateFrame(int choiceNum) {
        // Current scene
        String prevScene = currScene.name;
        // Get next scene
        Scene scene;
        // Only get next scene if a choice was made
        if (choiceNum != -1) {
            // set new stat
            int sceneChoiceIndex = buttonToChoiceIndex[choiceNum];
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
            // Only update pictures and audio if choice was made
            updatePictures();
            updateAudio();
            // Update stats if needed
            updateStats();

            if (prevScene.equals(currScene.name)) {
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

    private void updatePictures() {
        mainPanel.removeAll();
        if (!currScene.pictures.isEmpty()) {
            for (Picture picture : currScene.pictures) {
                ImageIcon icon = new ImageIcon(picture.getImg());
                JPanel imagePanel = new JPanel(new BorderLayout());
                imagePanel.setOpaque(false);
                JLabel imageLabel = new JLabel(icon);
                imagePanel.add(imageLabel, evaluateIconPosition(picture.getPosition()));
                mainPanel.add(imagePanel);
            }
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private String evaluateIconPosition(String position) {
        switch (position) {
            case "center":
                return BorderLayout.CENTER;
            case "top":
                return BorderLayout.NORTH;
            case "bottom":
                return BorderLayout.SOUTH;
            case "left":
                return BorderLayout.WEST;
            case "right":
                return BorderLayout.EAST;
            default:
                System.out.println("Invalid position, setting to center: " + position);
                return BorderLayout.CENTER;
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

    private void updateStats() {
        if (statsLabel == null) {
            return;
        }
        String text = "";
        for (String stat : Game.numberStats.keySet()) {
            text += " " + stat + ": " + Game.numberStats.get(stat) + " |";
        }
        for (String stat : Game.stringStats.keySet()) {
            text += " " + stat + ": " + Game.numberStats.get(stat) + " |";
        }

        statsLabel.setText(text.substring(0, text.length()-2));
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
                buttonToChoiceIndex[buttonIndex] = i;
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

    private void playAudio(String audioPath, boolean isBgm) {
        try {
            File audioFile = new File(audioPath);

            // Check if we're playing bgm or a sound effect
            if (isBgm) {
                currBgm = audioPath;
                bgmStream = AudioSystem.getAudioInputStream(audioFile);
                bgmClip = AudioSystem.getClip(null);
                bgmClip.open(bgmStream);
                bgmClip.start();
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                soundStream = AudioSystem.getAudioInputStream(audioFile);
                soundClip = AudioSystem.getClip(null);
                soundClip.open(soundStream);
                soundClip.start();
            }

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    private void stopAudio(boolean isBgm) {
        if (isBgm) {
            // Check if the clip is running
            if (bgmClip.isRunning()) {
                bgmClip.stop();
            }
            if (bgmClip.isOpen()) {
                bgmClip.close();
            }
        } else {
            if (soundClip.isRunning()) {
                soundClip.stop();
            }
            if (soundClip.isOpen()) {
                soundClip.close();
            }
        }
    }

    private void updateAudio() {
        // Check if current scene has bgm
        // If yes, check if equal to the last bgm; if not equal, switch the audio; do nothing otherwise
        // If no, check if there is currently any bgm playing; if so, stop it
        if (currScene.bgmFile != null) {
            if (!currScene.bgmFile.equals(currBgm)) {
                if (bgmStream != null) stopAudio(true);
                playAudio(currScene.bgmFile, true);
            }
        } else {
            if (bgmStream != null) stopAudio(true);
        }
        // Do same process for sound effects
        if (currScene.soundFile != null) {
            if (soundStream != null) stopAudio(false);
            playAudio(currScene.soundFile, false);
        } else {
            if (soundStream != null) stopAudio(false);
        }
    }

}
