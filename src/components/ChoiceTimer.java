package components;

import lib.Node;

public class ChoiceTimer extends Node {
    private int limit;
    private String nextScene;

    @Override
    public void parse() {
        limit = Integer.parseInt(tokenizer.getAndCheckNext("[1-9][0-9]*"));
        tokenizer.getAndCheckNext("next scene");
        nextScene = tokenizer.getNext();
    }

    public int getLimit() {return limit;}

    public String getNextScene() {return nextScene;}
}
