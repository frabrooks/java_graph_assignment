package graph_entities;

import java.util.ArrayList;
import java.util.Optional;

// You return your results using this class. If all required
// information is present, you get full marks. If you omit a solution
// to a particular question, leave the corresponding field absent.

public class Result<T>
{

        // A path found by an algorithm:
        private Optional< ArrayList< IVertex<T> > > path = Optional.empty();

        // For path finding algorithms, record all visited vertices
	private Optional< ArrayList< IVertex<T> > > visitedVertices = Optional.empty();

        // The total path cost in a found path, if a path has been found:
	private Optional< Float > pathCost = Optional.empty();

	public void setPath(ArrayList< IVertex<T> > path)
	{
		this.path = Optional.of(path);
	}

	public Optional< ArrayList< IVertex<T> > > getPath()
	{
		return this.path;
	}

	public void setVisitedVertices(ArrayList< IVertex<T> > visitedVertices)
	{
		this.visitedVertices = Optional.of(visitedVertices);
	}

	public Optional< ArrayList< IVertex<T> > > getVisitedVertices()
	{
		return this.visitedVertices;
	}

	public void setPathCost(Float pathCost)
	{
		this.pathCost = Optional.of(pathCost);
	}

	public Optional< Float > getPathCost()
	{
		return this.pathCost;
	}
}
