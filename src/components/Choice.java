package components;

import lib.Node;

import java.util.Arrays;

public class Choice extends Node {
    public String text;
    public String next;
    public String statString;
    public String conditionalString;

    @Override
    public void parse() {
        String currToken = tokenizer.checkNext();
        while (tokenizer.checkToken("text|next scene|conditional|change stat")) {
            tokenizer.getNext(); // consume the property token
            switch (currToken) {
                case "text":
                    if (text != null) {
                        // Already got a text for choice
                        // If see another text, then it's the scene's text
                        return;
                    }
                    // trim double quotes from start and end
                    text = tokenizer.getNext();
                    break;
                case "next scene":
                    next = tokenizer.getNext();
                    break;
                case "conditional":
                    conditionalString = tokenizer.getNext();
                    checkConditional();
                    break;
                case "change stat":
                    statString = tokenizer.getNext();
                    checkSetStat();
                    break;
                default:
                    System.out.println("Invalid token for choice: " + currToken);
                    System.exit(1);
                    break;
            }
            currToken = tokenizer.checkNext();
        }
        // TODO no error handling when 'next' doesn't exist (yet?) because sequential parsing
        // maybe put into parse Game at the end
        if (next == null) {
            System.out.println("Choice \"" + text + "\" is missing a next scene");
            System.exit(1);
        }
    }

    private void checkConditional() {
        String[] statArr = splitStat(conditionalString);
        String stat = statArr[0];
        String op = statArr[1];
        Integer numStat = Game.numberStats.get(stat);
        String stringStat = Game.stringStats.get(stat);

        if ((numStat != null && op.matches("==|>=|<=|>|<")) ||
                (stringStat != null && op.matches("=="))) {
            System.out.println("Scene Condition valid: " + conditionalString);
        } else {
            System.out.println("Scene Condition: " + conditionalString + " is not valid");
            System.exit(1);
        }
    }

    private void checkSetStat() {
        String[] statArr = splitStat(statString);
        String stat = statArr[0];
        String op = statArr[1];
        Integer numStat = Game.numberStats.get(stat);
        String stringStat = Game.stringStats.get(stat);
        if ((numStat != null && op.matches("\\+|-|\\*|/|=")) ||
                (stringStat != null && op.matches("="))) {
            System.out.println("Change Stat valid: " + statString);
        } else {
            System.out.println("Change Stat: " + statString + " is not valid");
            System.exit(1);
        }
    }

    private String[] splitStat(String statInput) {
        String[] statString = statInput.split(" ");
        if (statString.length < 3) {
            System.out.println("Stat " + statInput + " - missing stat name, operator, or value");
            System.exit(1);
        }
        int len = statString.length;
        String statName = String.join(" ", Arrays.copyOfRange(statString, 0, len - 2));
        String op = statString[len - 2];
        String right = statString[len - 1]; // value has no spaces
        return new String[]{statName, op, right};
    }

    public boolean evalConditional() {
        String[] statArr = splitStat(conditionalString);
        String stat = statArr[0];
        String op = statArr[1];
        String right = statArr[2];
        Integer numStat = Game.numberStats.get(stat);
        String stringStat = Game.stringStats.get(stat);

        if (numStat != null) {
            System.out.println("Evaluated num conditional");
            return evalNumStat(numStat, op, right);
        } else if (stringStat != null) {
            System.out.println("Evaluated string conditional");
            return stringStat.equals(right);
        } else {
            System.out.println("Stat: " + stat + " is not declared");
            System.exit(1);
        }
        return false;
    }

    private boolean evalNumStat(int left, String op, String right) {
        int comp = 0;
        try {
            comp = Integer.parseInt(right);
        } catch (NumberFormatException e) {
            System.out.println("Stat operand: " + right + " is not an integer");
            System.exit(1);
        }
        switch (op) {
            case "==":
                return left == comp;
            case ">=":
                return left >= comp;
            case "<=":
                return left <= comp;
            case ">":
                return left > comp;
            case "<":
                return left < comp;
            default:
                System.out.println("Unsupported arithmetic operation: " + op);
                System.exit(1);
        }
        return false;
    }

    public void setStat() {
        String[] statArr = splitStat(statString);
        String stat = statArr[0];
        String op = statArr[1];
        String right = statArr[2];
        Integer numStat = Game.numberStats.get(stat);
        String stringStat = Game.stringStats.get(stat);

        if (numStat != null) {
            setNumStat(stat, numStat, op, right);
            System.out.println("Set stat: " + stat + " to " +
                    Game.numberStats.get(stat));
        } else if (stringStat != null && op.matches("=")) {
            Game.stringStats.put(stringStat, right);
            System.out.println("Set stat: " + stat + " to " +
                    Game.stringStats.get(stat));
        } else {
            System.out.println("SetStat stat:" + stat + " is not declared");
            System.exit(1);
        }
    }

    private void setNumStat(String name, int left, String op, String right) {
        int comp = 0;
        try {
            comp = Integer.parseInt(right);
        } catch (NumberFormatException e) {
            System.out.println("Stat operand: " + right + " is not an integer");
            System.exit(1);
        }
        switch (op) {
            case "+":
                Game.numberStats.put(name, left + comp);
                break;
            case "-":
                Game.numberStats.put(name, left - comp);
                break;
            case "*":
                Game.numberStats.put(name, left * comp);
                break;
            case "/":
                Game.numberStats.put(name, left / comp);
                break;
            case "=":
                Game.numberStats.put(name, comp);
                break;
            default:
                System.out.println("Unsupported arithmetic operation: " + op);
                System.exit(1);
        }
    }
}
