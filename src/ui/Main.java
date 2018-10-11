package ui;

import components.Game;
import lib.Node;
import lib.Tokenizer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static List<String> literals = Arrays.asList(
            "GAME",
            "START SCENE",
            "title",
            "description",
            "lives",
            "stats",
            "STORY SCENES",
            "text",
            "choice",
            "next scene",
            "timer",
            "bgm",
            "sound effect",
            "picture",
            "file",
            "conditional",
            "change stat",
            "position",
            "DEATH SCENES",
            "END SCENES"
    );

    public static Map<String, Node> gameParts = new HashMap<>();

    public static void main(String[] args) {
        Tokenizer.makeTokenizer("./src/samples/a_sample.txt", literals);
        Game game = new Game();
        game.parse();

        // For Testing
        System.out.println("(✿╹◡╹) COMPILED");
        System.out.println("Game Title: " + game.title + "\n" +
                "Game Description" + game.description + "\n" +
                "Lives" + game.lives + "\n" +
                "Numeric Stats" + game.numberStats + "\n" +
                "String Stats" + game.stringStats + "\n" +
                "Start Scene" + game.startScene + "\n" +
                "Story Scenes" + game.storyScenes + "\n" +
                "Death Scenes" + game.deathScenes + "\n" +
                "End Scenes" + game.endScenes
        );
        System.out.println("(✿^◡^) GUCCI");
    }
}
