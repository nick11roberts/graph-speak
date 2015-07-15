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
    private final Boolean DEBUG = false;

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

    public List<List<Vertex>> parsedTextToVertices(List<List<String>> parsedText){
        List<List<Vertex>> vertexMatrix = new ArrayList<List<Vertex>>();

        for (int i = 0; i <= parsedText.size() - 1; i++){
            vertexMatrix.add(new ArrayList<Vertex>());
            for (int j = 0; j <= parsedText.get(i).size() - 1; j++){
                if (!(parsedText.get(i).get(j).equals("")
                        || parsedText.get(i).get(j).equals(" ")
                        || parsedText.get(i).get(j) == null)){
                    vertexMatrix.get(i).add(new Vertex(parsedText.get(i).get(j)));
                }
            }
        }

        for (int i = parsedText.size() - 1; i >= 0; i--)
            if (vertexMatrix.get(i).isEmpty())
                vertexMatrix.remove(i);

        System.out.println(vertexMatrix);

        return vertexMatrix;
    }
}
