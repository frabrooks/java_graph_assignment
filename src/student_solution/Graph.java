package student_solution;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import graph_entities.IEdge;
import graph_entities.IGraph;
import graph_entities.IVertex;
import graph_entities.Label;
import graph_entities.Result;


public class Graph<T> implements IGraph<T>
 {
    
    HashMap<String, IVertex<T>> vertices = new HashMap<String, IVertex<T>>();
    
    public Graph(){};
    
    @Override
    public void addVertex(String vertexId, IVertex<T> vertex) {
        vertices.put(vertexId, vertex);
    }

    @Override
    public void addEdge(String vertexSrcId, String vertexTgtId, Float cost) {
        IVertex<T> source = vertices.get(vertexSrcId);
        IVertex<T> target = vertices.get(vertexTgtId);
        if(source == null || target == null){
            return;
        }
        source.addEdge(new Edge<T>(target, cost));
    }

    @Override
    public Collection<IVertex<T>> getVertices() {
        return vertices.values();
    }

    @Override
    public Collection<String> getVertexIds() {
        return (Collection<String>) vertices.keySet();
    }

    @Override
    public IVertex<T> getVertex(String vertexId) {
        return vertices.get(vertexId);
    }
     // TODO: Implement interface IGraph appropriately
    
    @Override
    public String toDotRepresentation(){
        
        String result = "digraph { \n\t\t\t ";
        Collection<IVertex<T>> c = vertices.values();
        for(IVertex<T> vertex: c){
            result += vertex.getLabel().getName() + "\n\t\t\t";
        }
        Collection<IEdge<T>> e;
        for(IVertex<T> vertex: c){
            e = vertex.getSuccessors();
            for(IEdge<T> edge: e){
                result += vertex.getLabel().getName() + " -> ";
                result += edge.getTgt().getLabel().getName() + "[label=\"";
                result += edge.getCost() + "\"];\n\t\t\t";
            }
        }
        result += "\n}\n";
        return result;
    }
    
    @Override
    public void fromDotRepresentation(String dotFilePath)
    {
        String error = "Error, incorrect dot File format read from: " 
                    + dotFilePath + " graph has not been entirely initialised.  ";
        boolean digraphFound = false;
        
        
        try(BufferedReader br = new BufferedReader(
                        new FileReader(dotFilePath));){
            
            String nextLine = br.readLine();
            for( /**/ ; nextLine != null; nextLine = br.readLine()){
                
                //replace all whitespace characters with regex
                nextLine = nextLine.replaceAll("\\s","");
                
                //start of digraph
                if(nextLine.equals("digraph{")){
                    digraphFound = true;
                    continue;
                }
                
                //empty line
                if(nextLine.equals("")){
                    continue;
                }
                
                //end of digraph reached
                if(nextLine.equals("}")){
                    return;
                }
                
                // edge case
                if(nextLine.contains("->") && digraphFound){
                    String[] split;
                    //input should be of the form:  
                    //          string_vertex_A -> string_vertex_B[label="<number>"];
                    
                    //regex split on '->', '[label="<', and '>"];' to give:
                    //          [string_vertex_A, string_vertex_B, number]
                    split = nextLine.split("(->)+|(\\[label=\")+|(\"];)+");
                    if(split.length != 3){
                        System.err.println(error + " incorrect array length read ");
                    }
                    Float f = Float.parseFloat(split[2]);
                    addEdge(split[0], split[1], f);
                    continue;
                }
                
                // base case = new node to add
                if(digraphFound){
                    Label<T> l = new Label<T>();
                    l.setName(nextLine);
                    addVertex(nextLine, new Vertex<T>(l));
                    continue;
                }
                
                // if dot file is formatted correctly then we 
                // shouldn't ever end up here thanks to 'continue'
                System.err.println(error);
            }
                        
            
        } catch (FileNotFoundException e) {
            System.err.println(error + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println(error + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e){
            System.err.println(error + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public Result<T> breadthFirstSearchFrom(String vertexId, Predicate<IVertex<T>> pred) {

        ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
        Queue<IVertex<T>> queue = new LinkedList<IVertex<T>>();
        Map<IVertex<T>, IVertex<T>> parentMap = new HashMap<IVertex<T>, IVertex<T>>();
        Result<T> result = new Result<T>();

        IVertex<T> start = vertices.get(vertexId);
        if (start == null) {
            return result;
        }

        queue.add(start);
        IVertex<T> currentVertex = start;
        visited.add(start);

        while (queue.isEmpty() == false) {
            currentVertex = queue.remove();
            if (pred.test(currentVertex)) {
                break;
            } else {
                for (IEdge<T> v : currentVertex.getSuccessors()) {
                    if (!visited.contains(v.getTgt())) {
                        visited.add(v.getTgt());
                        queue.add(v.getTgt());
                        parentMap.put(v.getTgt(), currentVertex);
                    }
                }
            }
        }

        if (!visited.isEmpty()) {
            result.setVisitedVertices(
                    new ArrayList<IVertex<T>>(visited.subList(0, visited.indexOf(currentVertex) + 1)));
        }

        if (!pred.test(currentVertex)) {
            // No path found
            return result;
        }

        // get path and cost
        ArrayList<IVertex<T>> path = new ArrayList<IVertex<T>>();
        path.add(currentVertex);
        float cost = 0;
        for (IVertex<T> v = parentMap.get(currentVertex); v != null; v = parentMap.get(v)) {
            float temp = Float.MAX_VALUE;
            for (IEdge<T> e : v.getSuccessors()) {
                if (e.getTgt().equals(path.get(path.size() - 1))) {
                    if (e.getCost() < temp) {
                        temp = e.getCost();
                    }
                }
            }
            cost += temp;
            path.add(v);
        }

        Collections.reverse(path);

        result.setPath(path);
        result.setPathCost(cost);

        return result;
    }

    @Override
    public Result<T> depthFirstSearchFrom(String vertexId, Predicate<IVertex<T>> pred) {
        ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
        Map<IVertex<T>, IVertex<T>> parentMap = new HashMap<IVertex<T>, IVertex<T>>();
        Result<T> result = new Result<T>();

        IVertex<T> startVertex = vertices.get(vertexId);
        if (startVertex == null) {
            return result;
        }
        visited.add(startVertex);
        IVertex<T> endVertex = startVertex;
        boolean found = dfs(endVertex, startVertex, pred, visited, parentMap);

        // Set Visited nodes in result
        if (!visited.isEmpty()) {
            if (endVertex.equals(startVertex)) {
                // No path found. No node matched predicate
                result.setVisitedVertices(visited);
            } else {
                // Path was found so truncate list to final node
                result.setVisitedVertices(
                        new ArrayList<IVertex<T>>(visited.subList(0, visited.indexOf(endVertex) + 1)));
            }
        }

        if (!found) {
            // No path found
            return result;
        }

        // get path and cost
        ArrayList<IVertex<T>> path = new ArrayList<IVertex<T>>();
        path.add(visited.get(visited.size() - 1));
        float cost = 0;
        for (IVertex<T> v = parentMap.get(visited.get(visited.size() - 1)); v != null; v = parentMap.get(v)) {
            float temp = Float.MAX_VALUE;
            for (IEdge<T> e : v.getSuccessors()) {
                if (e.getTgt().equals(path.get(path.size() - 1))) {
                    if (e.getCost() < temp) {
                        temp = e.getCost();
                    }
                }
            }
            cost += temp;
            path.add(v);
        }

        Collections.reverse(path);

        result.setPath(path);
        result.setPathCost(cost);

        return result;
    }
    
    private boolean dfs(IVertex<T> storeVertex, IVertex<T> v, Predicate<IVertex<T>> pred, ArrayList<IVertex<T>> visited,
            Map<IVertex<T>, IVertex<T>> parentMap) {
        if (pred.test(v)) {
            storeVertex = v;
            return true;
        } else {
            for (IEdge<T> e : v.getSuccessors()) {
                if (!visited.contains(e.getTgt())) {
                    visited.add(e.getTgt());
                    parentMap.put(e.getTgt(), v);
                    if (dfs(storeVertex, e.getTgt(), pred, visited, parentMap)) {
                        return true;
                    }
                }

            }
            return false;
        }

    }
    
    @Override
    public Result<T> dijkstraFrom(String vertexId, Predicate<IVertex<T>> pred) {
        
        // Variables
        ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
        ArrayList<IVertex<T>> evaluatedNodes = new ArrayList<IVertex<T>>();
        Map<IVertex<T>, Float> costMap = new HashMap<IVertex<T>, Float>();
        Map<IVertex<T>, IVertex<T>> routeMap = new HashMap<IVertex<T>, IVertex<T>>();
        boolean pathFound = false;
        boolean noMoreNodes = true;
        Float currentSmallest = Float.MAX_VALUE;// Used in loop to select next node
        Result<T> result = new Result<T>();
        
        // Check starting vertex exists
        IVertex<T> startVertex = vertices.get(vertexId);
        if (startVertex == null) {
            return result;
        }
        // Initialise CostMap with max values
        for (IVertex<T> v : getVertices()) {
            costMap.put(v, Float.MAX_VALUE);
        }
        // Start vertex obviously costs 0
        costMap.put(startVertex, 0.0f);
        visited.add(startVertex);
        
        // ------------------- Dijkstra Algorithm ------------
        IVertex<T> currentVertex = startVertex;
        while (true) {
            evaluatedNodes.add(currentVertex);
            if (pred.test(currentVertex)) {
                pathFound = true;
                break;
            }
            for (IEdge<T> v : currentVertex.getSuccessors()) {
                if (evaluatedNodes.contains(v.getTgt())) {
                    continue;
                }
                if (!visited.contains(v.getTgt())) {
                    visited.add(v.getTgt());
                }
                Float f = costMap.get(currentVertex) + v.getCost();
                if (f <= costMap.get(v.getTgt())) {
                    costMap.put(v.getTgt(), f);
                    routeMap.put(v.getTgt(), currentVertex);
                }
            }
            currentSmallest = Float.MAX_VALUE;
            noMoreNodes = true;
            IVertex<T> potentialNext;
            for(IVertex<T> v: evaluatedNodes){
                for(IEdge<T> e: v.getSuccessors()){
                    potentialNext = e.getTgt();
                    if(currentSmallest >= costMap.get(potentialNext) && !evaluatedNodes.contains(potentialNext)){
                        noMoreNodes = false;
                        currentSmallest = costMap.get(potentialNext);
                        currentVertex = potentialNext;
                    }
                }
            }
            // If true then every entry in the costMap, and therefore
            // every node, must be in 'evaluatedNodes' list and no path exists
            if (noMoreNodes) {
                System.out.println("Error: dijkstra call with unreachable node");
                break;
            }
        }
        // -------------------- Algorithm End -----------------------------
        
        if(visited.isEmpty() == false){
            result.setVisitedVertices(visited);
        }
        if(pathFound == false){
            return result;
        }
        
        // Last evaluated node = target when a path has been found
        IVertex<T> end = evaluatedNodes.get(evaluatedNodes.size()-1);
        result.setPathCost(costMap.get(end));
        
        // Get path
        ArrayList<IVertex<T> > path = new ArrayList<IVertex<T>>();
        path.add(end);
        while(path.get(path.size()-1).equals(startVertex) == false){
            path.add(routeMap.get(path.get(path.size()-1)));
        }
        Collections.reverse(path);
        result.setPath(path);
        
        return result;
    }
    
    @Override
    public Result<T> aStar(String startVertexId, String endVertexId, BiFunction< IVertex<T>, IVertex<T>, Float > heuristics)
    {
        // Variables
        ArrayList<IVertex<T>> visited = new ArrayList<IVertex<T>>();
        ArrayList<IVertex<T>> evaluatedNodes = new ArrayList<IVertex<T>>();
        Map<IVertex<T>, Float> costMap = new HashMap<IVertex<T>, Float>();
        Map<IVertex<T>, IVertex<T>> routeMap = new HashMap<IVertex<T>, IVertex<T>>();
        boolean pathFound = false;
        boolean noMoreNodes = true;
        Float currentSmallest = Float.MAX_VALUE;// Used in loop to select next
                                                // node
        Result<T> result = new Result<T>();

        // Check starting and end vertex exist
        IVertex<T> startVertex = vertices.get(startVertexId);
        IVertex<T> endVertex = vertices.get(endVertexId);
        if (startVertex == null || endVertex == null) {
            System.out.println("Error: aStar call with unknown nodes");
            return result;
        }
        // Initialise CostMap with max values
        for (IVertex<T> v : getVertices()) {
            costMap.put(v, Float.MAX_VALUE);
        }
        // Start vertex obviously costs 0
        costMap.put(startVertex, heuristics.apply(startVertex, endVertex));
        visited.add(startVertex);

        // ------------------- A* Algorithm ------------
        IVertex<T> currentVertex = startVertex;
        while (true) {
            evaluatedNodes.add(currentVertex);
            if (heuristics.apply(currentVertex, endVertex) == 0.0f) {
                pathFound = true;
                break;
            }
            for (IEdge<T> v : currentVertex.getSuccessors()) {
                if (evaluatedNodes.contains(v.getTgt())) {
                    continue;
                }
                if (!visited.contains(v.getTgt())) {
                    visited.add(v.getTgt());
                }
                Float estimatedCost = costMap.get(currentVertex) 
                                        + v.getCost() 
                                        + heuristics.apply(v.getTgt(), endVertex)
                                        - heuristics.apply(currentVertex, endVertex);
                if (estimatedCost <= costMap.get(v.getTgt())) {
                    costMap.put(v.getTgt(), estimatedCost);
                    routeMap.put(v.getTgt(), currentVertex);
                }
            }
            currentSmallest = Float.MAX_VALUE;
            noMoreNodes = true;
            IVertex<T> potentialNext;
            for (IVertex<T> v : evaluatedNodes) {
                for (IEdge<T> e : v.getSuccessors()) {
                    potentialNext = e.getTgt();
                    if (currentSmallest >= costMap.get(potentialNext) && !evaluatedNodes.contains(potentialNext)) {
                        noMoreNodes = false;
                        currentSmallest = costMap.get(potentialNext);
                        currentVertex = potentialNext;
                    }
                }
            }
            // If true then every entry in the costMap, and therefore
            // every node, must be in 'evaluatedNodes' list and no path exists
            if (noMoreNodes) {
                System.out.println("Error: aStar call with unreachable node");
                break;
            }
        }
        // -------------------- Algorithm End -----------------------------

        if (visited.isEmpty() == false) {
            result.setVisitedVertices(visited);
        }
        if (pathFound == false) {
            return result;
        }

        // Last evaluated node = target when a path has been found
        IVertex<T> end = evaluatedNodes.get(evaluatedNodes.size() - 1);
        result.setPathCost(costMap.get(end));

        // Get path
        ArrayList<IVertex<T>> path = new ArrayList<IVertex<T>>();
        path.add(end);
        while (path.get(path.size() - 1).equals(startVertex) == false) {
            path.add(routeMap.get(path.get(path.size() - 1)));
        }
        Collections.reverse(path);
        result.setPath(path);

        return result;
    }
    
 }




     