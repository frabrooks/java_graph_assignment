import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import graph_entities.IVertex;
import graph_entities.Result;
import student_solution.Graph;

public class GraphExercise {
    public static void main(String[] args){
        
        // Load and initialise test graph
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
        
        // Load graph specifically designed to test A* algorithm
        // Graph has a junction at node 'c' where the shortest edge
        // leads away from the target towards node 'd'. Dijkstra 
        // would follow shortest edge up to node 'e' and find dead
        // end. A* should use heuristics outlined below to deduce 
        // that c->d leads far away from target node 't' and not 
        // expand that path. Graph snippet: 
        // 
        //              (e)h=18
        //               |
        //               |1
        //              (d)h=16     <- A* should never expand up here
        //               |             as heuristic increases massively
        //               |1
        //       --2--> (c)h=7
        //               |2
        //               v
        //              (f)h=5
        //               |
        //          (*to target*)
        //
        String aStarGraphPath = "aStarTestGraph.dot";
        Graph<Object> aStarGraph = new Graph<Object>();
        aStarGraph.fromDotRepresentation(aStarGraphPath);
        
        // Fake heuristics to test the A* algorithm on the particular graph outlined above
        final class PseudoHeuristics implements BiFunction< IVertex<Object>, IVertex<Object>, Float >{

            @Override
            public Float apply(IVertex<Object> t, IVertex<Object> u) {
                // assume that 't' is always target
                switch(t.getLabel().getName()){
                case "a":
                    return 10f;
                case "b":
                    return 8f;
                case "c":
                    return 7f;
                case "d":
                    return 16f;
                case "e":
                    return 18f;
                case "f":
                    return 5f;
                case "g":
                    return 2f;
                case "t":
                    return 0f;
                default:
                    return Float.MAX_VALUE;
                }
            }
            
        }
        
        BiFunction< IVertex<Object>, IVertex<Object>, Float > heuristics = new PseudoHeuristics();

        try (PrintWriter out = new PrintWriter("aStarResult.dot");) {

            out.append("A* SEARCH (a -> t): \n");
            out.append(printResult(aStarGraph.aStar("a", "t", heuristics)));

            out.append("A* SEARCH (IMPOSSIBLE): \n");
            out.append(printResult(aStarGraph.aStar("a", "z", heuristics)));

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
