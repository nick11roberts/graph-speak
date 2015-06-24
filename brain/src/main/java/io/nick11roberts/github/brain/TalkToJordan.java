package io.nick11roberts.github.brain;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

import java.util.List;

import javax.inject.Named;

import static io.nick11roberts.github.brain.OfyService.ofy;

@Api(name = "talkToJordan", version = "v1", namespace = @ApiNamespace(ownerDomain = "brain.github.nick11roberts.io", ownerName = "brain.github.nick11roberts.io", packagePath = ""))
public class TalkToJordan {

    @ApiMethod(name = "prompt")
    public ResponseStatement prompt(@Named("inputStatement") String inputStatement) {

        TrainingExampleManager tem = new TrainingExampleManager();
        List<List<Vertex>> parsedVertexMatrix = tem.parsedTextToVertices(tem.parseText(inputStatement));

        for (int i = 0; i <= parsedVertexMatrix.size() - 1; i++){
            for (int j = 0; j <= parsedVertexMatrix.get(i).size() - 1; j++){
                ofy().save().entity(parsedVertexMatrix.get(i).get(j)).now();
                if (j >= 1) {
                    if (findEdgeRecord(parsedVertexMatrix.get(i).get(j - 1), parsedVertexMatrix.get(i).get(j)) == null) {
                        ofy().save().entity(
                                new Edge(parsedVertexMatrix.get(i).get(j - 1), parsedVertexMatrix.get(i).get(j))
                        ).now();
                    }else{
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
                        });
                    }
                }
            }
        }

        ResponseStatement response = new ResponseStatement(inputStatement);

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

}
