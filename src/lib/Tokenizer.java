/*
 * Taken from tinyDotStarter from CS410
 */

package lib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {

    private static String program;
    private static List<String> literals;
    private String[] tokens;
    private int currentToken;
    private static Tokenizer theTokenizer;

    // Constructor
    private Tokenizer(String filename, List<String> literalsList){
        literals = literalsList;
        try {
            program = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Didn't find file");
            System.exit(0);
        }
        tokenize();
    }

    // Converts the program string to an array of tokens
    private void tokenize() {
        String tokenizedProgram = program;
        tokenizedProgram = tokenizedProgram.replace("\t","");
        System.out.println(program);

        // For all literals found in the string, except for those surrounded by double quotes, surround with _
        for (String s : literals){
            // Regex taken from: https://stackoverflow.com/questions/22755023/regex-to-replace-all-comma-except-enclosed-in-double-quotes-java
            tokenizedProgram = tokenizedProgram.replaceAll(s + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)","_"+s+"_");
            System.out.println(tokenizedProgram);
        }

        tokenizedProgram = tokenizedProgram.replaceAll("__","_");
        System.out.println(tokenizedProgram);
        String [] temparray = tokenizedProgram.split("[:_\n]");

        // Remove null elements
        List<String> x = new ArrayList<>(Arrays.asList(temparray));
        x.removeAll(Arrays.asList("", null));
        System.out.println(x);

        // Put into tokens; trimming will be handled in parsing
        tokens = new String[x.size()];
        tokens = x.toArray(tokens);
        System.out.println(Arrays.asList(tokens));
    }

    private String checkNext(){
        String token="";
        if (currentToken<tokens.length){
            token = tokens[currentToken];
        }
        else
            token="NO_MORE_TOKENS";
        return token;
    }

    public String getNext(){
        String token="";
        if (currentToken<tokens.length){
            token = tokens[currentToken];
            currentToken++;
        }
        else
            token="NULLTOKEN";
        return token;
    }


    public boolean checkToken(String regexp){
        String s = checkNext();
        System.out.println("comparing: "+s+"  to  "+regexp);
        if (!s.matches(regexp)) {
            System.out.println("checktoken " + s + " failed");
        }
        return (s.matches(regexp));
    }


    public String getAndCheckNext(String regexp){
        String s = getNext();
        if (!s.matches(regexp)) System.exit(0);
        System.out.println("matched: "+s+"  to  "+regexp);
        return s;
    }

    public boolean moreTokens(){
        return currentToken<tokens.length;
    }

    public static void makeTokenizer(String filename, List<String> literals){
        if (theTokenizer==null){
            theTokenizer = new Tokenizer(filename,literals);
        }
    }

    public static Tokenizer getTokenizer(){
        return theTokenizer;
    }
}
