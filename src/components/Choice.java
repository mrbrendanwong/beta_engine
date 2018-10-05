package components;

import lib.Node;
import ui.Main;

import java.util.Arrays;

public class Choice extends Node {
    public String text;
    public String next;
    public boolean conditional = true;

    @Override
    public void parse() {
        String currToken = tokenizer.checkNext();
        while (!Main.literals.contains(currToken)) {
            tokenizer.getNext(); // consume the property token
            switch (currToken) {
                case "text":
                    // trim double quotes from start and end
                    text = trimQuotes(tokenizer.getNext());
                    break;
                case "next":
                    next = tokenizer.getNext();
                    break;
                case "conditional":
                    conditional = evalConditional(trimQuotes(tokenizer.getNext()));
                    break;
                case "change stat":
                    setStat(trimQuotes(tokenizer.getNext()));
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

    private String trimQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
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

    private boolean evalConditional(String statInput) {
        String[] statArr = splitStat(statInput);
        String stat = statArr[0];
        String op = statArr[1];
        String right = statArr[2];
        Integer numStat = Game.numberStats.get(stat);
        String stringStat = Game.stringStats.get(stat);

        if (numStat != null) {
            return evalNumStat(numStat, op, right);
        } else if (stringStat != null) {
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

    private void setStat(String statInput) {
        String[] statArr = splitStat(statInput);
        String stat = statArr[0];
        String op = statArr[1];
        String right = statArr[2];
        Integer numStat = Game.numberStats.get(stat);
        String stringStat = Game.stringStats.get(stat);

        if (numStat != null) {
            setNumStat(stat, numStat, op, right);
        } else if (stringStat != null) {
            Game.stringStats.put(stringStat, right);
        } else {
            System.out.println("Stat " + stat + " is not declared");
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

    @Override
    public void evaluate() {

    }
}
