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
    private JPanel textPanel;
    private JPanel interactionPanel;
    private JButton nextButton;
    private List<JButton> choiceButtons = new ArrayList<>();

    private JPanel timerPanel;
    private Timer timer;
    private int count = 0;

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

        // Create timer
        timerPanel = new JPanel();
        statusPanel.add(timerPanel);

        // Create choiceButtons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            choiceButtons.add(new JButton());
            choiceButtons.get(i).setActionCommand(String.format("%d", i));
            choiceButtons.get(i).addActionListener(this);
        }

        // Set start scene to the current scene
        currScene = game.startScene;
        if (currScene.bgmFile != null) playAudio(currScene.bgmFile, true);
        if (currScene.soundFile != null) playAudio(currScene.soundFile, false);


        // Add text and choiceButtons and stuff
        updateFrame("", -1);
    }

    public void launchFrame(){
        frame.setVisible(true);
    }

    // Updates current elements of game on the frame
    // Pass a sceneName to force scene change
    private void updateFrame(String sceneName, int choiceNum) {
        // Current scene
        Scene prevScene = currScene;
        // Get next scene
        Scene scene;
        // Only get new scene if a choice was made
        // or forced scene change
        if (choiceNum != -1 || !sceneName.isEmpty()) {
            String nextScene;
            // reset text pointer
            currTextPointer = 0;

            if (sceneName.isEmpty()) {
                // set new stat
                // stats tied to choices, timeouts can't set stat
                int sceneChoiceIndex = buttonToChoiceIndex[choiceNum];
                Choice c = currScene.choices.get(sceneChoiceIndex);
                if (c.statString != null) {
                    c.setStat();
                }
                nextScene = currScene.choices.get(sceneChoiceIndex).next;
            } else {
                nextScene = sceneName;
            }

            if (game.storyScenes.containsKey(nextScene)) {
                currScene = game.storyScenes.get(nextScene);
            } else if (game.deathScenes.containsKey(nextScene)) {
                currScene = game.deathScenes.get(nextScene);
            } else if (game.endScenes.containsKey(nextScene)) {
                currScene = game.endScenes.get(nextScene);
            }

            // Only update audio if choice was made
            updateAudio();

            // Comment: Let the end user worry about infinite scene loops (?)

            if (prevScene.equals(currScene.name)) {
                System.out.println("Infinite scene loop");
                // TODO Probably show something?
                System.exit(1);
            }

        }


        // if there's still text to display, then display it, if not then display the choiceButtons
        if (currTextPointer < currScene.texts.size()) {
            updateText();
            updateTimer(false);
        } else {
            updateButtons();
            updateTimer(true);
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

    private void updateTimer(boolean endOfText) {
        if (currScene.timer != null) {
            if (endOfText) {
                // Start the timer only when choices appear
                int limit = currScene.timer.getLimit();
                String timeoutScene = currScene.timer.getNextScene();

                JLabel label = new JLabel(Integer.toString(limit));
                timerPanel.add(label);
                timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (count < limit) {
                            label.setText(Integer.toString(limit - count));
                        } else {
                            ((Timer) (e.getSource())).stop();
                            timerPanel.removeAll();
                            timerPanel.revalidate();
                            timerPanel.repaint();
                            System.out.println("NOW YOU FUCKED UP");

                            // Force the next scene
                            System.out.println("Timed out, went to scene: " + timeoutScene);
                            updateFrame(timeoutScene, -1);
                        }
                        count++;
                    }
                });
                timer.setInitialDelay(0);
                timer.start();
            }
        } else {
            // No timer on this scene, clear it
            if (timer != null) {
                timer.stop();
                timerPanel.removeAll();
                timerPanel.revalidate();
                timerPanel.repaint();
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        updateFrame("", Integer.parseInt(e.getActionCommand()));
    }

    private void playAudio(String audioPath, boolean isBgm) {
        try {
            File audioFile = new File(audioPath);

            // Check if we're playing bgm or a sound effect
            if (isBgm) {
                currBgm = audioPath;
                bgmStream = AudioSystem.getAudioInputStream(audioFile);
                bgmClip = AudioSystem.getClip();
                bgmClip.open(bgmStream);
                bgmClip.start();
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                soundStream = AudioSystem.getAudioInputStream(audioFile);
                soundClip = AudioSystem.getClip();
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
            bgmClip.close();
        } else {
            if (soundClip.isRunning()) {
                soundClip.stop();
            }
            soundClip.close();
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
