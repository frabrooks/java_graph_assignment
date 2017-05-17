import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Predicate;

import graph_entities.IVertex;
import graph_entities.Result;
import student_solution.Graph;

public class GraphExercise {
    public static void main(String[] args){
        String dotGraphPath = "graph_example_custom.dot";
        
        Graph<Object> graph = new Graph<Object>();
        
        graph.fromDotRepresentation(dotGraphPath);
        
        try (PrintWriter out = new PrintWriter("outputTest.dot");){
            out.append(graph.toDotRepresentation());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Predicate<IVertex<Object>> pred = v -> v.getLabel().getName().equals("q");
        Predicate<IVertex<Object>> predDijkstra1 = v -> v.getLabel().getName().equals("q");
        Predicate<IVertex<Object>> predDijkstra2 = v -> v.getLabel().getName().equals("t");
        Predicate<IVertex<Object>> unreachablePred = v -> v.getLabel().getName().equals("tihs is not a node");
        
        try (PrintWriter out = new PrintWriter("result.dot");){
            
            out.append("BREADTH FIRST SEARCH (a -> q): \n");
            out.append(printResult(graph.breadthFirstSearchFrom("a", pred)));
            
            out.append("DEPTH FIRST SEARCH (a -> q): \n");
            out.append(printResult(graph.depthFirstSearchFrom("a", pred)));
            
            out.append("BREADTH FIRST SEARCH (impossible): \n");
            out.append(printResult(graph.breadthFirstSearchFrom("a", unreachablePred)));
            
            out.append("DEPTH FIRST SEARCH (impossible): \n");
            out.append(printResult(graph.depthFirstSearchFrom("a", unreachablePred)));
            
            out.append("DIJKSTRA ALGORITHM (a -> q): \n");
            out.append(printResult(graph.dijkstraFrom("a", predDijkstra1)));
            
            out.append("DIJKSTRA ALGORITHM (a -> t): \n");
            out.append(printResult(graph.dijkstraFrom("a", predDijkstra2)));
            
            out.append("DIJKSTRA ALGORITHM (g -> t): \n");
            out.append(printResult(graph.dijkstraFrom("g", predDijkstra2)));
            
            out.append("DIJKSTRA ALGORITHM (impossible): \n");
            out.append(printResult(graph.dijkstraFrom("a", unreachablePred)));
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        
    }
    
    private static String printResult(Result<Object> r){
        String result = "\n";
        
        if(r.getPath().isPresent()){
            result += "PATH: \n";
            for(IVertex<Object> v: r.getPath().get() ){
                result += "\t" + v.getLabel().getName() + "\n";
            }
        }else{
            result += "NO PATH FOUND \n";
        }
        result += "\n\n";
        
        
        if(r.getVisitedVertices().isPresent()){
            result += "VISITED VERTICES: \n";
            for(IVertex<Object> v: r.getVisitedVertices().get() ){
                result += "\t" + v.getLabel().getName() + "\n";
            }
        }else{
            result += "NO VERTICES VISITED \n";
        }
        result += "\n\n";
        
        
        if(r.getPathCost().isPresent()){
            result += "COST: \n\t";
            result += r.getPathCost().get();
            result += "\n";
        }else{
            result += "NO COST CALCULATED \n";
        }
        result += "\n\n";
        
        return result;
    }
    
}
