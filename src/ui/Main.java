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
        Tokenizer.makeTokenizer("generic_sample.txt", literals);
        Game game = new Game();
        game.parse();
        System.out.println("(✿╹◡╹) ");
        System.out.println(game.description);
        System.out.println(game.lives);
        System.out.println(game.title);
    }
}
