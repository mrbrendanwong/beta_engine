package components;

import lib.Node;

public class Picture extends Node {
    private String file;
    private String position;

    @Override
    public void parse() {
        while (tokenizer.checkNext().equals("file") || tokenizer.checkNext().equals("position")) {
            switch (tokenizer.checkNext()) {
                case "file":
                    tokenizer.getAndCheckNext("file");
                    file = tokenizer.getNext();
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

    public String getFile() {
        return file;
    }

    public String getPosition() {
        return position;
    }

    public String toString() {
        return "(" + file + ", " + position + ")";
    }
}
