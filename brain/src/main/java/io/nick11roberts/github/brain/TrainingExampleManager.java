package io.nick11roberts.github.brain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 6/23/15.
 */
public class TrainingExampleManager {
    public TrainingExampleManager(){}

    private List<List<String>> parsedText = new ArrayList<List<String>>();

    public List<List<String>> parseText(String text){

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
