package components;

import lib.Node;
import ui.Main;

import java.util.ArrayList;
import java.util.List;

public class Scene extends Node {
    public String name;
    public List<String> texts = new ArrayList<>();
    public List<Choice> choices = new ArrayList<>();
    public ChoiceTimer timer;

    public String bgmFile;
    public String soundFile;
    public String pictureFile;
    public String picturePosition = "center";

    @Override
    public void parse() {
        name = tokenizer.getNext();
        while (true) {
            switch (tokenizer.checkNext()) {
                case "text":
                    tokenizer.getAndCheckNext("text");
                    while (!Main.literals.contains(tokenizer.checkNext()) && !tokenizer.checkNext().equals("NO_MORE_TOKENS")) {
                        texts.add(tokenizer.getNext());
                    }
                    break;
                case "choice":
                    tokenizer.getAndCheckNext("choice");
                    Choice c = new Choice();
                    c.parse();
                    choices.add(c);
                    break;
                case "timer":
                    if (timer != null) {
                        System.out.println("Duplicate timer in scene \"" + name + "\"");
                        System.exit(1);
                    }
                    tokenizer.getAndCheckNext("timer");
                    timer = new ChoiceTimer();
                    timer.parse();
                    break;
                case "bgm":
                    tokenizer.getAndCheckNext("bgm");
                    bgmFile = tokenizer.getNext();
                    break;
                case "sound effect":
                    tokenizer.getAndCheckNext("sound effect");
                    soundFile = tokenizer.getNext();
                    break;
                case "picture":
                    tokenizer.getAndCheckNext("picture");
                    while (tokenizer.checkNext().equals("file") || tokenizer.checkNext().equals("position")) {
                        switch (tokenizer.checkNext()) {
                            case "file":
                                tokenizer.getAndCheckNext("file");
                                pictureFile = tokenizer.getNext();
                                break;
                            case "position":
                                tokenizer.getAndCheckNext("position");
                                picturePosition = tokenizer.getAndCheckNext("center|left|right|top|bottom");
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    return;
            }
        }
    }

    // For testing
    @Override
    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", texts=" + texts +
                ", choices=" + choices +
                ", timer=" + timer +
                ", bgmFile='" + bgmFile + '\'' +
                ", soundFile='" + soundFile + '\'' +
                ", pictureFile='" + pictureFile + '\'' +
                ", picturePosition='" + picturePosition + '\'' +
                '}';
    }
}
