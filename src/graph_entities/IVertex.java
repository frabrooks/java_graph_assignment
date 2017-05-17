package graph_entities;

import java.util.Collection;

// Vertices are comparable according to their corresponding *current*
// cost stored in their labels

// Different vertex exploration policies based on this cost, or not
// define different graph search algorithms

public interface IVertex<T> extends Comparable<IVertex<T>>
{
  // Add an edge to this vertex.
  void addEdge(IEdge<T> edge);

  // We get all the edges emanating from this vertex:  
  Collection< IEdge<T> > getSuccessors();

  // See class Label for an an explanation:
  Label<T> getLabel();
  void setLabel(Label<T> label);
}
