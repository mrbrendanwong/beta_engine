package components;

import lib.Node;
import ui.Main;

import java.util.ArrayList;
import java.util.List;

public class Scene extends Node {
    public String name;
    public List<String> texts = new ArrayList<>();
    public List<Choice> choices = new ArrayList<>();
    public int timer = 0;

    public String bgmFile;
    public String soundFile;
    public List<Picture> pictures = new ArrayList<>();

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
                    tokenizer.getAndCheckNext("timer");
                    timer = Integer.parseInt(tokenizer.getNext());
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
                    Picture p = new Picture();
                    p.parse();
                    pictures.add(p);
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
                ", pictures='" + pictures + '\'' +
                '}';
    }
}
