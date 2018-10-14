package components;

import lib.Node;
import ui.Main;

import java.util.HashMap;

public class Game extends Node {
    public String title;
    public String description;
    public int lives;

    public Scene startScene;
    public static HashMap<String, Integer> numberStats = new HashMap<>();
    public static HashMap<String, String> stringStats = new HashMap<>();
    public HashMap<String, Scene> storyScenes = new HashMap<>();
    public HashMap<String, Scene> deathScenes = new HashMap<>();
    public HashMap<String, Scene> endScenes = new HashMap<>();


    @Override
    public void parse() {
        tokenizer.getAndCheckNext("GAME");
        while (tokenizer.moreTokens()) {
            String statKey;
            String statValue;
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
                    if (lives < 0) {
                        System.out.println("Lives can't be less than 0");
                        System.exit(1);
                    }
                    break;
                case "stats":
                    // adds next 2 tokens to stats if they aren't one of the literals
                    while(!Main.literals.contains(tokenizer.checkNext())) {
                        statKey = tokenizer.getNext();
                        statValue = tokenizer.getNext();
                        // tries parsing statValue as an int, and if it fails, then it places it in the stringStats
                        try {
                            numberStats.put(statKey, Integer.parseInt(statValue));
                        } catch (NumberFormatException e) {
                            stringStats.put(statKey, statValue);
                        }
                    }
                    break;
                case "START SCENE":
                    startScene = new Scene();
                    startScene.parse();
                    break;
                case "STORY SCENES":
                    while(!Main.literals.contains(tokenizer.checkNext()) && !tokenizer.checkNext().equals("NO_MORE_TOKENS")) {
                        scene = new Scene();
                        scene.parse();
                        checkSceneName(scene.name);
                        storyScenes.put(scene.name, scene);
                    }
                    break;
                case "DEATH SCENES":
                    while(!Main.literals.contains(tokenizer.checkNext()) && !tokenizer.checkNext().equals("NO_MORE_TOKENS")) {
                        scene = new Scene();
                        scene.parse();
                        checkSceneName(scene.name);
                        deathScenes.put(scene.name, scene);
                    }
                    break;
                case "END SCENES":
                    while(!Main.literals.contains(tokenizer.checkNext()) && !tokenizer.checkNext().equals("NO_MORE_TOKENS")) {
                        scene = new Scene();
                        scene.parse();
                        checkSceneName(scene.name);
                        endScenes.put(scene.name, scene);
                    }
                    break;
                default:
                    System.out.println("Invalid token: " + currToken);
                    System.exit(1);
            }
        }
    }

    private void checkSceneName (String sceneName) {
        if (storyScenes.containsKey(sceneName) ||
                deathScenes.containsKey(sceneName) ||
                endScenes.containsKey(sceneName)) {
            System.out.println("Scene \"" + sceneName + "\" already exists!");
            System.exit(1);
        }
    }
}
