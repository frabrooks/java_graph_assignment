package graph_entities;

import java.util.Optional;

// A Label labels a vertex with a number of pieces of information: 


public class Label<T>
{
    // Information that may be used to manage and track the graph
    // search.  Must be present if the vertex with this label has a
    // parent, and absent otherwise. It must never be null.
    private Optional<IVertex<T>> parentVertex;

    // Current cost to reach the vertex that has this label. May be
    // used in search algorithms.
    private Float cost;

    // The name of the vertex with this label, in particular used for
    // the dot file.
    private String name;
    
    // Any additional information stored by the vertex with this
    // label.
    private T data;

    public Label()
    {
        this.cost = 0.0F;
        this.parentVertex = Optional.empty();
        this.name = "UNAMED";
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }


    public Float getCost()
    {
        return this.cost;
    }

    public void setCost(Float cost)
    {
        this.cost = cost;
    }

    // Visitation parent
    public void setParentVertex(IVertex<T> parentVertex)
    {
        if(parentVertex == null)
            this.parentVertex = Optional.empty();
        else
            this.parentVertex = Optional.of(parentVertex);
    }

    public Optional<IVertex<T>> getParentVertex()
    {
        return this.parentVertex;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
