package io.nick11roberts.github.brain;

/**
 * Created by nick on 6/22/15.
 */
public class ResponseStatement {

    public ResponseStatement(){}

    public ResponseStatement(String statement){
        setStatement(statement);
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getStatement(){
        return statement;
    }

    private String statement;
}
