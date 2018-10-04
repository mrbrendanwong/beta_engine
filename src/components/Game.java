package components;

import lib.Node;
import ui.Main;

import java.util.ArrayList;
import java.util.List;

public class Game extends Node {
    public String title;
    public String description;
    public int lives;
    public Scene startScene;
    public List<Integer> stats = new ArrayList<>();
    public List<Node> storyScenes = new ArrayList<>();
    public List<Node> deathScenes = new ArrayList<>();
    public List<Node> endScenes = new ArrayList<>();


    @Override
    public void parse() {
        tokenizer.getAndCheckNext("GAME");
        while (tokenizer.moreTokens()) {
            Scene scene;
            String currToken = tokenizer.getNext();
            switch (currToken) {
                case "title":
                    title = tokenizer.getNext();
                    break;
                case "description":
                    description = tokenizer.getNext();
                    break;
                case "lives":
                    lives = Integer.parseInt(tokenizer.getNext());
                    break;
                case "stats":
                    // figure out how to tokenize stats
                    tokenizer.getNext();
                    break;
                case "START SCENE":
                    startScene = new Scene();
                    startScene.parse();
                    break;
                case "STORY SCENES":
                    while(!Main.literals.contains(tokenizer.checkNext())) {
                        scene = new Scene();
                        scene.parse();
                        storyScenes.add(scene);
                    }
                case "DEATH SCENES":
                    while(!Main.literals.contains(tokenizer.checkNext())) {
                        scene = new Scene();
                        scene.parse();
                        deathScenes.add(scene);
                    }
                case "END SCENES":
                    while(!Main.literals.contains(tokenizer.checkNext())) {
                        scene = new Scene();
                        scene.parse();
                        endScenes.add(scene);
                    }
                default:
                    System.out.println("Invalid token: " + currToken);
                    tokenizer.getNext();
                    break;
            }
        }
        System.out.println(storyScenes);
    }

    @Override
    public void evaluate() {

    }
}
