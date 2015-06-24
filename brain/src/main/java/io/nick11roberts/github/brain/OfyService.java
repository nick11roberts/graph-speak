package io.nick11roberts.github.brain;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Created by nick on 6/23/15.
 */
public class OfyService {
    static {
        factory().register(Edge.class);
        factory().register(Vertex.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}