package client;

import components.Choice;
import components.Game;
import components.Scene;
import lib.Node;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client implements ActionListener {
    private int NUM_BUTTONS = 4;
    private Font FONT_STYLE = new Font("Courier New", Font.PLAIN, 16);

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

        statusPanel = new JPanel();
        statusPanel.setBackground(Color.BLACK);

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);

        interactionPanel = new JPanel();
        interactionPanel.setBackground(Color.BLACK);


        // Set status panel
        statusPanel.setLayout(new BorderLayout());

        if (Game.stringStats.size() != 0 || Game.numberStats.size() != 0) {
            statsLabel = new JLabel("", SwingConstants.CENTER);
            statsLabel.setForeground(Color.WHITE);
            statsLabel.setFont(FONT_STYLE);
            statusPanel.add(statsLabel, BorderLayout.CENTER);
        }
        // TODO if there is a timer, initialize timerLabel
        timer = new JLabel("", SwingConstants.CENTER);
        timer.setForeground(Color.WHITE);
        timer.setFont(FONT_STYLE);
        statusPanel.add(timer, BorderLayout.SOUTH);
        timer.setText("Time remaining: 30");

        // Set status and interaction panel sizes
        statusPanel.setMinimumSize(new Dimension(0, 50));
        interactionPanel.setMinimumSize(new Dimension(0, 200));

        statusPanel.setPreferredSize(statusPanel.getMinimumSize());
        interactionPanel.setPreferredSize(interactionPanel.getMinimumSize());

        GridLayout buttonLayout = new GridLayout(2, 2);
        interactionPanel.setLayout(buttonLayout);

        frame.add(statusPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(interactionPanel, BorderLayout.SOUTH);

        interactionPanel.setBorder(BorderFactory.createEmptyBorder());
        if (timer != null || statsLabel != null) {
            statusPanel.setBorder(BorderFactory.createEmptyBorder());
        }

        // Set the text panel
        textPanel = new JPanel();
        textPanel.setBackground(Color.BLACK);

        List<Color> colorList = Arrays.asList(new Color(53,133,255),
                new Color(1, 102, 255),
                new Color(0,87,218),
                new Color(0, 70,176));

        // Create the button that says "Next"
        nextButton = new JButton("Next");
        nextButton.setActionCommand(String.format("%d", -1));
        nextButton.addActionListener(this);
        nextButton.setBackground(Color.BLUE);
        nextButton.setForeground(Color.WHITE);
        nextButton.setFont(FONT_STYLE);
        nextButton.setBorder(BorderFactory.createEmptyBorder());
        nextButton.setFocusable(false);

        // Create choiceButtons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            JButton choiceButton = new JButton();
            choiceButton.setFont(FONT_STYLE);
            choiceButton.setBackground(colorList.get(i));
            choiceButton.setForeground(Color.WHITE);
            choiceButton.setBorder(BorderFactory.createEmptyBorder());
            choiceButton.setFocusable(false);

            choiceButtons.add(choiceButton);
            choiceButtons.get(i).setActionCommand(String.format("%d", i));
            choiceButtons.get(i).addActionListener(this);
        }

        // Set start scene to the current scene
        currScene = game.startScene;
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
            // Only update audio if choice was made
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

    private void updateText() {
        interactionPanel.removeAll();
        textPanel.removeAll();

        JLabel textLabel = new JLabel(currScene.texts.get(currTextPointer));
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(FONT_STYLE);
        textLabel.setBorder(BorderFactory.createEmptyBorder());

        textPanel.add(textLabel);
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
