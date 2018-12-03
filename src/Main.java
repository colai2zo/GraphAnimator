
import javafx.scene.layout.Pane;

import java.util.HashSet;
import java.util.Stack;

public class Main {

    public static void main(String[] args){

        Graph graph = new Graph();

        graph.addNode(250,400);
        graph.addNode(50,200);
        graph.addNode(250,0);
        graph.addNode(250,400);
        graph.addNode(450,100);
        graph.addNode(450,300);

        Node node1 = graph.getNodeAt(250,400);
        Node node2 = graph.getNodeAt(50,200);
        Node node3 = graph.getNodeAt(250,0);
        Node node4 = graph.getNodeAt(250,4);
        Node node5 = graph.getNodeAt(450,100);
        Node node6 = graph.getNodeAt(450,300);

        System.out.println(graph.getNodes().size());

        graph.addEdge(node1,node2);
        graph.addEdge(node1,node4);
        graph.addEdge(node2,node5);
        graph.addEdge(node3,node4);
        graph.addEdge(node3,node6);
        graph.addEdge(node4,node5);

        System.out.println("GRAPH\n" + graph);

        HashSet<Node>[] partition = graph.bipartition();
        if(partition == null) System.out.println("NULL");
        else{
            HashSet<Node> leftSide = partition[0];
            HashSet<Node> rightSide = partition[1];
            System.out.println("LEFT:");
            for(Node n : leftSide){
                System.out.println(n);
            }
            System.out.println("RIGHT:");
            for(Node n : rightSide){
                System.out.println(n);
            }
        }


    }
}
