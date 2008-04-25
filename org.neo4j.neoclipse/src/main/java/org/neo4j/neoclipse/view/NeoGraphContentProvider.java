/*
 * NeoGraphContentProvider.java
 */
package org.neo4j.neoclipse.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;

/**
 * Provides the elements that must be displayed in the graph.
 * 
 * @author  Peter H&auml;nsgen
 */
public class NeoGraphContentProvider implements IGraphEntityRelationshipContentProvider
{
    /**
     * The view.
     */
    protected NeoGraphViewPart view;
    
    /**
     * The constructor.
     */
    public NeoGraphContentProvider(NeoGraphViewPart view)
    {
        this.view = view;
    }
    
    /**
     * Returns the relationships between the given nodes.
     */
    public Object[] getRelationships(Object source, Object dest)
    {
        Node start = (Node) source;
        Node end = (Node) dest;
        
        List<Relationship> rels = new ArrayList<Relationship>();
        
        Iterable<Relationship> rs = start.getRelationships(Direction.OUTGOING);
        for (Relationship r : rs)
        {
            if (r.getEndNode().getId() == end.getId())
            {
                rels.add(r);
            }
        }            
        
        return rels.toArray();
    }

    /**
     * Returns all nodes the given node is connected with.
     */
    public Object[] getElements(Object inputElement)
    {
        Node node = (Node) inputElement;
        
        Map<Long, Node> nodes = new HashMap<Long, Node>();            
        getElements(node, nodes, view.getTraversalDepth());

        return nodes.values().toArray();
    }
    
    /**
     * Determines the connected nodes within the given traversal depth.
     */
    private void getElements(Node node, Map<Long, Node> nodes, int depth)
    {
        // add the start node too
        nodes.put(node.getId(), node);
        
        if (depth > 0)
        {
            Iterable<Relationship> rs = node.getRelationships(Direction.INCOMING);
            for (Relationship r : rs)
            {
                Node start = r.getStartNode();
                if (!nodes.containsKey(start.getId()))
                {
                    nodes.put(start.getId(), start);
            
                    getElements(start, nodes, depth - 1);
                }
            }

            rs = node.getRelationships(Direction.OUTGOING);
            for (Relationship r : rs)
            {
                Node end = r.getEndNode();
                if (!nodes.containsKey(end.getId()))
                {
                    nodes.put(end.getId(), end);
                
                    getElements(end, nodes, depth - 1);
                }
            }
        }
    }
    
    public void dispose()
    {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }        
}