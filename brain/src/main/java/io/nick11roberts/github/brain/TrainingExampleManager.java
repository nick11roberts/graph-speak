package io.nick11roberts.github.brain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nick on 6/23/15.
 */
public class TrainingExampleManager {
    public TrainingExampleManager(){}

    private List<List<String>> parsedText = new ArrayList<List<String>>();
    private final Boolean DEBUG = true;

    public List<List<String>> parseText(String text){
        String[] textSentence = text.split("(?<=[.!?])\\s*"); //Splits text into sentences
        for (int i = 0; i <= textSentence.length-1; i++) {
            parsedText.add(Arrays.asList(textSentence[i].toLowerCase().replaceAll("[^a-z ]", "").split(" ")));
        }

        if (DEBUG) {
            System.out.println(parsedText);
        }

        return parsedText;
    }

    public List<List<String>> getParsedText(){
        return parsedText;
    }

    public Boolean learnExample(List<List<String>> parsedText){
        Boolean success = false;

        return success;
    }
}
