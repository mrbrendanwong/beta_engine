package components;

import lib.Node;

import java.util.ArrayList;
import java.util.List;

public class Scene extends Node {
    public String sceneName;
    public List<String> texts = new ArrayList<>();
    public List<Choice> choices = new ArrayList<>();
    public int timer;

    public String bgmFile;
    public String soundFile;
    public String pictureFile;
    public String picturePosition;

    @Override
    public void parse() {
        // Check for scene name
        // Check for texts
        // Check for choices
        // Check for timer
        // Check for bgm
        // Check for picture file; if picture exists, check for position
    }

    @Override
    public void evaluate() {

    }
}
