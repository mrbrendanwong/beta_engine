package ui;

import client.Client;
import components.Game;
import lib.Tokenizer;

import java.util.Arrays;
import java.util.List;

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

    public static void main(String[] args) {
        String filePath;
        if (args.length > 0) {
            filePath = args[0];

            Tokenizer.makeTokenizer(filePath, literals);
            Game game = new Game();
            game.parse();
            System.out.println("(✿^◡^) GUCCI");

            // Launch game window
            Client client = new Client(game);
            client.launchFrame();
        } else {
            System.err.println("Please provide an input .txt file");
            System.exit(1);
        }
    }
}
