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

    }

    @Override
    public void evaluate() {

    }
}
