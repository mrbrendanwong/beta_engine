package components;

import lib.Node;
import ui.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scene extends Node {
    public String name;
    public List<String> texts = new ArrayList<>();
    public List<Choice> choices = new ArrayList<>();
    public ChoiceTimer timer;

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
                    checkFile(bgmFile);
                    break;
                case "sound effect":
                    tokenizer.getAndCheckNext("sound effect");
                    soundFile = tokenizer.getNext();
                    checkFile(soundFile);
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

    private void checkFile(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            System.out.println("\"" + filepath + "\" doesn't exist");
            System.exit(1);
        }
    }

}
