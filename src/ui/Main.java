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
            "STORY SCENE",
            "text",
            "choice",
            "next",
            "timer",
            "bgm",
            "sound effect",
            "picture",
            "file",
            "conditional",
            "stat",
            "position",
            "DEATH SCENE",
            "END SCENE"
    );

    public static Map<String, Node> gameParts = new HashMap<>();

    public static void main(String[] args) {
        Tokenizer.makeTokenizer("generic_sample.txt", literals);
        Game game = new Game();
        game.parse();
        game.evaluate();
    }
}
