package io.nick11roberts.github.brain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by nick on 6/22/15.
 */

@Entity
public class Edge {

    private Edge(){}

    public Edge(Vertex vertexFrom, Vertex vertexTo){
        setRetentionIndex(2.0);
        setRetention(0.0);
        setVertexFrom(vertexFrom);
        setVertexTo(vertexTo);
        constructVertexTuple();
        traverse();
    }

    private Double retentionIndex;

    private Double retention;

    @Index
    private Ref<Vertex> vertexFrom;

    @Index
    private Ref<Vertex> vertexTo;

    @Id
    private String vertexTuple;

    public void traverse(){
        setRetention(Math.log(getRetentionIndex()));
        setRetentionIndex(getRetentionIndex() + 1.0);
    }

    public Double getRetentionIndex() {
        return retentionIndex;
    }

    public void setRetentionIndex(Double retentionIndex) {
        this.retentionIndex = retentionIndex;
    }

    public Double getRetention() {
        return retention;
    }

    public void setRetention(Double retention) {
        this.retention = retention;
    }

    public Vertex getVertexFrom() {
        return vertexFrom.get();
    }

    public void setVertexFrom(Vertex vertexFrom) {
        this.vertexFrom = Ref.create(vertexFrom);
    }

    public Vertex getVertexTo() {
        return vertexTo.get();
    }

    public void setVertexTo(Vertex vertexTo) {
        this.vertexTo = Ref.create(vertexTo);
    }

    public void constructVertexTuple(){
        if ((vertexFrom != null) && (vertexTo != null)){
            vertexTuple = "(" + getVertexFrom().getWord() + ", " + getVertexTo().getWord() + ")";
        }
    }

    public String getVertexTuple(){
        return vertexTuple;
    }
}
