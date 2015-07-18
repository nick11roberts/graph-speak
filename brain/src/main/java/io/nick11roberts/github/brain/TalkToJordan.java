package io.nick11roberts.github.brain;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Named;

@Api(name = "talkToJordan", version = "v1", namespace = @ApiNamespace(ownerDomain = "brain.github.nick11roberts.io", ownerName = "brain.github.nick11roberts.io", packagePath = ""))
public class TalkToJordan {

    private DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> wordNetwork = new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);

    private final int RESPONSE_LENGTH_VARIATION = 3;


    @ApiMethod(name = "getState")
    public ResponseStatement getState(){
        StringNameProvider<String> vertexLabel = new StringNameProvider<String>(){
            public String getVertexName(String p) {
                return p;
            }
        };

        EdgeNameProvider<Integer> edgeLabel = new IntegerEdgeNameProvider<Integer>();

        DOTExporter<String, DefaultWeightedEdge> exporter =
                new DOTExporter<>(vertexLabel, null, null, null, null); //Use a different constructor
        StringWriter w = new StringWriter();
        exporter.export(w, wordNetwork);

        ResponseStatement responseWrapper = new ResponseStatement();


        try {
            responseWrapper.setStatement(URLEncoder.encode(w.toString(),"UTF-8"));
            System.out.println(
                    URLEncoder.encode(responseWrapper.getStatement(),"UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Doesn't work properly. Ideally, this should generate a text file with these contents.
        return responseWrapper;
    }


    @ApiMethod(name = "prompt")
    public ResponseStatement prompt(@Named("inputStatement") String inputStatement) {
        int responseMaxSize = 0;
        ResponseStatement response = new ResponseStatement("Hey");
        TrainingExampleManager tem = new TrainingExampleManager();
        List<List<String>> parsedText = tem.parseText(inputStatement);
        for (int i = 0; i <= parsedText.size() - 1; i++){ // for number of sentences
            for (int j = 0; j <= parsedText.get(i).size() - 1; j++){ // for number of words per sentence
                //Add current vertex to the graph
                wordNetwork.addVertex(parsedText.get(i).get(j));
                //Recalculate response max size if needed
                if(parsedText.get(i).size() > responseMaxSize){
                    responseMaxSize = parsedText.get(i).size();
                }
                if (j >= 1) {
                    if (!wordNetwork.containsEdge(parsedText.get(i).get(j - 1), parsedText.get(i).get(j))){
                        //Add edge to graph
                        wordNetwork.addEdge(parsedText.get(i).get(j - 1), parsedText.get(i).get(j));
                        wordNetwork.setEdgeWeight(
                                wordNetwork.getEdge(parsedText.get(i).get(j - 1), parsedText.get(i).get(j)),
                                2.0
                        );
                    } else{
                        //Increment the edge weight
                        wordNetwork.setEdgeWeight(
                                wordNetwork.getEdge(parsedText.get(i).get(j - 1), parsedText.get(i).get(j)),
                                wordNetwork.getEdgeWeight(
                                        wordNetwork.getEdge(
                                                parsedText.get(i).get(j - 1),
                                                parsedText.get(i).get(j)
                                        )
                                ) + 0.5
                        );
                    }
                }
            }
        }

        //Response should be a random walk, so write a random walk function
        response.setStatement(
                randomWalkForResponse(
                        randInt(
                                responseMaxSize/RESPONSE_LENGTH_VARIATION,
                                responseMaxSize*RESPONSE_LENGTH_VARIATION
                        )
                )
        );

        return response;
    }

    private String randomWalkForResponse(int maxSize){
        //The response can be up to maxSize long
        String response = "";

        //Generate random punctuation
        Map<Integer, String> punctuationMap = new HashMap<>();
        punctuationMap.put(0,".");
        punctuationMap.put(1,"!");
        punctuationMap.put(2,"?");
        String punctuation = punctuationMap.get(randInt(0, punctuationMap.size() - 1));

        //Get a random vertex from the wordNetwork graph
        int randomVertexIndex = randInt(0, wordNetwork.vertexSet().size() - 1);
        String currentRandomVertex = wordNetwork.vertexSet().toArray()[randomVertexIndex].toString();

        //Initial update of response
        response += currentRandomVertex;

        //Walk
        for (int i = 0; i <= maxSize-2; i++){
            //Choose a new vertex if possible
            if (wordNetwork.outDegreeOf(currentRandomVertex) > 0) {

                //Space out each word
                response += " ";

                //Find new random vertex
                currentRandomVertex = wordNetwork
                        .getEdgeTarget(
                                (DefaultWeightedEdge) wordNetwork
                                        .outgoingEdgesOf(currentRandomVertex)
                                        .toArray()[randInt(0, wordNetwork.outDegreeOf(currentRandomVertex) - 1)]
                        );

                //Update response
                response += currentRandomVertex;
            } else {

                //Add punctuation and leave the loop, we have reached a leaf
                response+=punctuation;
                break;

            }
        }
        return response;
    }

    private static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
