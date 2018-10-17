package client;

import components.Choice;
import components.Game;
import components.Picture;
import components.Scene;

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
    private Color BG_COLOR = Color.BLACK;
    private Color TEXT_COLOR = Color.WHITE;

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
    private JLabel statsLabel;
    private JLabel countdown;
    private List<JButton> choiceButtons = new ArrayList<>();

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
        frame.setSize(1280, 970);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        statusPanel = new JPanel();
        statusPanel.setBackground(BG_COLOR);

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);

        interactionPanel = new JPanel();
        interactionPanel.setBackground(BG_COLOR);


        // Set status panel
        statusPanel.setLayout(new BorderLayout());

        if (Game.stringStats.size() != 0 || Game.numberStats.size() != 0) {
            statsLabel = new JLabel("", SwingConstants.CENTER);
            statsLabel.setForeground(TEXT_COLOR);
            statsLabel.setFont(FONT_STYLE);
            statusPanel.add(statsLabel, BorderLayout.CENTER);
        }

        countdown = new JLabel("", SwingConstants.CENTER);
        countdown.setForeground(TEXT_COLOR);
        countdown.setFont(FONT_STYLE);
        statusPanel.add(countdown, BorderLayout.SOUTH);

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

        interactionPanel.setBorder(BorderFactory.createEmptyBorder());
        if (timer != null || statsLabel != null) {
            statusPanel.setBorder(BorderFactory.createEmptyBorder());
        }

        // Set the text panel
        textPanel = new JPanel();
        textPanel.setBackground(BG_COLOR);

        List<Color> colorList = Arrays.asList(new Color(53,133,255),
                new Color(1, 102, 255),
                new Color(0,87,218),
                new Color(0, 70,176));

        // Create the button that says "Next"
        nextButton = new JButton("Next");
        nextButton.setActionCommand(String.format("%d", -1));
        nextButton.addActionListener(this);
        nextButton.setBackground(colorList.get(3));
        nextButton.setForeground(TEXT_COLOR);
        nextButton.setFont(FONT_STYLE);
        nextButton.setBorder(BorderFactory.createEmptyBorder());
        nextButton.setFocusable(false);

        // Create choiceButtons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            JButton choiceButton = new JButton();
            choiceButton.setFont(FONT_STYLE);
            choiceButton.setBackground(colorList.get(i));
            choiceButton.setForeground(TEXT_COLOR);
            choiceButton.setBorder(BorderFactory.createEmptyBorder());
            choiceButton.setFocusable(false);

            choiceButtons.add(choiceButton);
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
        updateFrame("", -1);
        updateStats();
    }

    public void launchFrame(){
        frame.setVisible(true);
    }

    // Updates current elements of game on the frame
    // Pass a sceneName to force scene change
    private void updateFrame(String sceneName, int choiceNum) {
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

            // Update stats pictures, audio, and stats upon choice being made
            updatePictures();
            updateAudio();
            updateStats();
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

        JLabel textLabel = new JLabel(currScene.texts.get(currTextPointer));
        textLabel.setForeground(TEXT_COLOR);
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
            text += " " + stat + ": " + Game.stringStats.get(stat) + " |";
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

    private void updateTimer(boolean endOfText) {
        if (currScene.timer != null) {
            if (endOfText) {
                // Start the timer only when choices appear
                int limit = currScene.timer.getLimit();
                String timeoutScene = currScene.timer.getNextScene();
                count = 0;

                timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (count < limit) {
                            countdown.setText("Timer: " + Integer.toString(limit - count));
                        } else {
                            ((Timer) (e.getSource())).stop();
                            countdown.setText("");
                            System.out.println("Ran out of time");

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
            }
            countdown.setText("");
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
