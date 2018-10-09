package lib;

public abstract class Node {
    protected Tokenizer tokenizer = Tokenizer.getTokenizer();

    abstract public void parse();
}
