package io.nick11roberts.github.brain;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import javax.inject.Named;

import static io.nick11roberts.github.brain.OfyService.ofy;

@Api(name = "talkToJordan", version = "v1", namespace = @ApiNamespace(ownerDomain = "brain.github.nick11roberts.io", ownerName = "brain.github.nick11roberts.io", packagePath = ""))
public class TalkToJordan {

    @ApiMethod(name = "prompt")
    public ResponseStatement prompt(@Named("inputStatement") String inputStatement) {

        ResponseStatement response = new ResponseStatement("Hey");

        TrainingExampleManager tem = new TrainingExampleManager();
        List<List<String>> parsedText = tem.parseText(inputStatement);
        List<List<Vertex>> parsedVertexMatrix = tem.parsedTextToVertices(parsedText);


        DefaultDirectedWeightedGraph<String, DefaultEdge> wordNetwork = new DefaultDirectedWeightedGraph<String, DefaultEdge>(DefaultEdge.class);

        for (int i = 0; i <= parsedText.size() - 1; i++){ // for number of sentences
            for (int j = 0; j <= parsedText.get(i).size() - 1; j++){ // for number of words per sentence
                //add current vertex to the graph
                wordNetwork.addVertex(parsedText.get(i).get(j));
                if (j >= 1) {
                    if (!wordNetwork.containsEdge(parsedText.get(i).get(j - 1), parsedText.get(i).get(j))){
                        //Add edge to graph
                        wordNetwork.addEdge(parsedText.get(i).get(j - 1), parsedText.get(i).get(j));
                    } else{
                        //Increment the edge weight
                        //Do this later I guess
                        /*
                        final Vertex vertexFrom = parsedVertexMatrix.get(i).get(j - 1);
                        final Vertex vertexTo = parsedVertexMatrix.get(i).get(j);

                        ofy().transact( new VoidWork() {
                            public void vrun() {
                                Edge edgeToBeTraversed = ofy().load().key(
                                        Key.create(Edge.class, findEdgeRecord(vertexFrom, vertexTo).getVertexTuple())
                                ).now();
                                edgeToBeTraversed.traverse();
                                ofy().save().entity(edgeToBeTraversed).now();
                            }
                        });*/
                    }
                }
            }
        }



        //ResponseStatement response = new ResponseStatement(generateResponse(10));

        return response;
    }

    private Edge findEdgeRecord(Vertex vertexFrom, Vertex vertexTo) {
        String searchableVertexTuple = "("
                + vertexFrom.getWord()
                + ", "
                + vertexTo.getWord()
                + ")";

        return ofy().load().type(Edge.class).id(searchableVertexTuple).now();
    }

    private String generateResponse(int inputStatementLength){
        Map<Integer, String> punctuation = new HashMap<>();
        punctuation.put(0,".");
        punctuation.put(1,"!");
        punctuation.put(2,"?");
        String response = "";
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Stack<List<Entity>> edgeQueryStack = new Stack<List<Entity>>();
        Stack<Vertex> vertexStack = new Stack<Vertex>();

        Query q = new Query("Vertex");
        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        Integer randomInitialWordIndex = randInt(0, results.size() - 1);
        com.google.appengine.api.datastore.Key currentVertexKey = results.get(randomInitialWordIndex).getKey();

        String currentWord = results.get(randomInitialWordIndex).getKey().getName();
        response += currentWord;

        for(int i = 0; i <= inputStatementLength-1; i++){
            //find list of connecting edges
            edgeQueryStack.push(
                    datastore.prepare(
                            new Query("Edge").setFilter(
                                    new Query.FilterPredicate(
                                            "vertexFromWord", Query.FilterOperator.EQUAL, currentWord
                                    )
                            )
                    ).asList(FetchOptions.Builder.withDefaults())
            );

            if(!edgeQueryStack.isEmpty() && edgeQueryStack.peek().size()>0) {
                //choose random edge, get next vertex, reset current word
                currentWord = (String)edgeQueryStack.peek().get(
                                randInt(0, edgeQueryStack.peek().size() - 1)
                        ).getProperty("vertexToWord");
                //append current word
                response += " " + currentWord;
            }
            edgeQueryStack.pop();
        }
        response += punctuation.get(randInt(0, punctuation.size()-1));
        response = response.substring(0, 1).toUpperCase() + response.substring(1);
        return response;
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
