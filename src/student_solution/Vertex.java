package student_solution;


import java.util.ArrayList;
import java.util.Collection;

import graph_entities.IEdge;
import graph_entities.IVertex;
import graph_entities.Label;

 public class Vertex<T> implements IVertex<T>
 {
    private ArrayList<IEdge<T>> edges = new ArrayList<IEdge<T>>();
    private Label<T> label;
    
    public Vertex(Label<T> l){
        this.label = l;
    }
    
    @Override
    public int compareTo(IVertex<T> arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void addEdge(IEdge<T> edge) {
        edges.add(edge);
    }

    @Override
    public Collection<IEdge<T>> getSuccessors() {
        return edges;
    }

    @Override
    public Label<T> getLabel() {
        return label;
    }

    @Override
    public void setLabel(Label<T> label) {
        this.label = label;
    }
     // TODO: Implement interface IVertex appropriately
 }

