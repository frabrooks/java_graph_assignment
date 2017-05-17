package graph_entities;

// An edge has a target vertex (Tgt) and a cost. 

public interface IEdge<T>
{
    IVertex<T> getTgt();
    Float getCost();
}
