package student_solution;

import graph_entities.IVertex;
import graph_entities.IEdge;

 public class Edge<T> implements IEdge<T>
 {
     
     private float weight;
     private IVertex<T> target;
     
     public Edge(IVertex<T> target, Float weight){
         this.target = target;
         this.weight = weight;
     }
     
    @Override
    public IVertex<T> getTgt() {
        return target;
    }

    @Override
    public Float getCost() {
        return weight;
    }

     // TODO: Implement IEdge appropriately 

 };


