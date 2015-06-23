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

    public Edge(Double retention, Vertex vertexTo, Vertex vertexFrom){
        setRetention(retention);
        setVertexFrom(vertexFrom);
        setVertexTo(vertexTo);
    }

    public Edge(Vertex vertexTo, Vertex vertexFrom){
        setRetention(1.0);
        setVertexFrom(vertexFrom);
        setVertexTo(vertexTo);
    }

    @Id
    private Long id; //Automatically generated

    private Double retention;

    @Index
    private Ref<Vertex> vertexFrom;

    @Index
    private Ref<Vertex> vertexTo;

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
}
