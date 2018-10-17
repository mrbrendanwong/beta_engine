package components;

import lib.Node;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Picture extends Node {
    private String fileToken;
    private String position;
    private BufferedImage img;

    @Override
    public void parse() {
        while (tokenizer.checkNext().equals("file") || tokenizer.checkNext().equals("position")) {
            switch (tokenizer.checkNext()) {
                case "file":
                    tokenizer.getAndCheckNext("file");
                    fileToken = tokenizer.getNext();
                    File file = new File(fileToken);
                    checkFileExist(file);
                    readImage(file);
                    break;
                case "position":
                    tokenizer.getAndCheckNext("position");
                    position = tokenizer.getAndCheckNext("center|left|right|top|bottom");
                    break;
                default:
                    break;
            }
        }
    }

    public BufferedImage getImg() {
        return img;
    }

    public String getPosition() {
        return position;
    }

    // For debugging
    public String toString() {
        return "(" + fileToken + ", " + position + ")";
    }

    // To make sure the file exists before trying to read it
    private void checkFileExist(File file) {
        if (!file.exists()) {
            System.out.println("\"" + fileToken + "\" doesn't exist");
            System.exit(1);
        }
    }

    // Read the file, and if the result is null, quit parsing
    private void readImage(File file) {
        try {
            img = ImageIO.read(file);
            if (img == null) {
                throw new IOException();
            }
        } catch (IOException e) {
            System.out.println("Was unable to read the image file \"" + file.getName() + "\"");
            System.exit(1);
        }
    }
}
