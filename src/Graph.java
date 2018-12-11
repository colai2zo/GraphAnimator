import javafx.scene.Cursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.*;

public class Graph extends Pane {

    private HashMap<Node, HashSet<Node>> nodes;
    private HashSet<Edge> edges;

    public Graph(){
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        nodes = new HashMap<>();
        edges = new HashSet<>();
    }

    public HashSet<Node> getNodes(){
        return new HashSet<>(nodes.keySet());
    }

    public HashSet<Edge> getEdges(){
        return new HashSet<>(edges);
    }

    /**
     *
     * @param x x-coordinate of new node
     * @param y y-coordinate of new node
     * @return true if node was added successfully, false otherwise.
     */
    public boolean addNode(double x, double y){
        return addNode(x,y,0);
    }

    public boolean addNode(double x, double y, double spacing){
        Node node = new Node(x,y);
        for(Node other : this.getNodes()){
            if(node.overlaps(other) || node.linearOverlaps(other, spacing)) return false;
        }
        nodes.put(node, new HashSet<>());
        getChildren().add(node);
        return true;
    }


    /**
     *
     * @param x x-coordinate of new node
     * @param y y-coordinate of new node
     * @return true if node was added successfully, false otherwise.
     */
    public boolean removeNode(double x, double y){
        Node nodeToRemove = getNodeAt(x, y);
        if(nodeToRemove == null) return false;

        ArrayList<Node> conntectedNodes = new ArrayList<>(this.nodes.get(nodeToRemove));

        for(int i = 0 ; i < conntectedNodes.size() ; i++){
            removeEdge(nodeToRemove, conntectedNodes.get(i));
        }

        getChildren().remove(nodeToRemove);
        nodes.remove(nodeToRemove);

        return true;
    }


    public boolean addEdge(Node node1, Node node2){
        Edge edge = new Edge(node1, node2);
        if(edges.contains(edge)) return false;
        nodes.get(node1).add(node2);
        nodes.get(node2).add(node1);
        edges.add(edge);
        getChildren().add(edge);
        return true;
    }


    public boolean removeEdge(Node node1, Node node2){
        Edge edgeToRemove = new Edge(node1, node2);
        if(!edges.contains(edgeToRemove)) return false;
        getChildren().remove(edgeToRemove);
        edges.remove(edgeToRemove);
        nodes.get(node1).remove(node2);
        nodes.get(node2).remove(node1);
        return true;
    }

    public boolean edgeBetween(Node node1, Node node2){
        return nodes.get(node1).contains(node2);
    }

    public Node getNodeAt(double x, double y){
        for(Node node : nodes.keySet()) if(node.contains(x,y)) return node;
        return null;
    }

    public boolean importGraph(File file){
        clear();
        if(file.getName().endsWith(".csv")){
            try{
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
                while((line = reader.readLine()) != null){
                    String[] coords = line.split("\",\"");
                    String currentNodeCoords = coords[0];
                    int xCoord = 0, yCoord = 0;
                    try{
                        xCoord = Integer.parseInt(currentNodeCoords.substring(currentNodeCoords.indexOf("(") + 1, currentNodeCoords.indexOf(",")).trim());
                        yCoord = Integer.parseInt(currentNodeCoords.substring(currentNodeCoords.indexOf(",") + 1, currentNodeCoords.indexOf(")")).trim());
                    }
                    catch (Exception e){
                        clear();
                        e.printStackTrace();
                        return false;
                    }

                    addNode(xCoord, yCoord);
                    Node currentNode = getNodeAt(xCoord, yCoord);

                    for(int i = 1 ; i < coords.length ; i++){
                        currentNodeCoords = coords[i];
                        try{
                            xCoord = Integer.parseInt(currentNodeCoords.substring(currentNodeCoords.indexOf("(") + 1, currentNodeCoords.indexOf(",")).trim());
                            yCoord = Integer.parseInt(currentNodeCoords.substring(currentNodeCoords.indexOf(",") + 1, currentNodeCoords.indexOf(")")).trim());
                        }
                        catch (Exception e){
                            clear();
                            e.printStackTrace();
                            return false;
                        }
                        addNode(xCoord, yCoord);
                        Node adjacent = getNodeAt(xCoord, yCoord);
                        addEdge(currentNode, adjacent);
                    }
                }
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
                return false;
            }
            catch (IOException e){
                e.printStackTrace();
                return false;
            }
            return true;
        }
        else if(file.getName().endsWith(".json")){
            try{
                String content = new Scanner(file).useDelimiter("\\Z").next().replaceAll("[\\s\\n]","");
                String[] nodeContent = content.split("],");

                for(int i = 0 ; i < nodeContent.length ; i++){

                    String currentNodeCoords = nodeContent[i].split(":")[0];
                    int xCoord = 0, yCoord = 0;
                    try{
                        xCoord = Integer.parseInt(currentNodeCoords.substring(currentNodeCoords.indexOf("(") + 1, currentNodeCoords.indexOf(",")));
                        yCoord = Integer.parseInt(currentNodeCoords.substring(currentNodeCoords.indexOf(",") + 1, currentNodeCoords.indexOf(")")));
                    }
                    catch(Exception e){
                        clear();
                        e.printStackTrace();
                        return false;
                    }
                    addNode(xCoord, yCoord);
                    Node currentNode = getNodeAt(xCoord, yCoord);

                    String[] adjacentNodeCoords = nodeContent[i].split(":")[1].split("\",\"");
                    if(adjacentNodeCoords.length == 1 && adjacentNodeCoords[0].equals("[")) continue;
                    for(int j = 0 ; j < adjacentNodeCoords.length ; j ++){

                        try{
                            xCoord = Integer.parseInt(adjacentNodeCoords[j].substring(adjacentNodeCoords[j].indexOf("(") + 1, adjacentNodeCoords[j].indexOf(",")));
                            yCoord = Integer.parseInt(adjacentNodeCoords[j].substring(adjacentNodeCoords[j].indexOf(",") + 1, adjacentNodeCoords[j].indexOf(")")));
                        }
                        catch(Exception e){
                            clear();
                            e.printStackTrace();
                            return false;
                        }

                        addNode(xCoord, yCoord);
                        Node adjacent = getNodeAt(xCoord, yCoord);
                        addEdge(currentNode, adjacent);

                    }
                }
            }
            catch(FileNotFoundException e){
                e.printStackTrace();
                return false;
            }
            return true;
        }
        else{
            return false;
        }
    }

    public boolean exportGraph(File file){
        if(file.getName().endsWith(".csv")){
            try{
                PrintWriter writer = new PrintWriter(file);
                StringBuilder builder = new StringBuilder();

                Iterator iterator = nodes.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry pair = (Map.Entry) iterator.next();
                    builder.append("\"(");
                    builder.append((int) ((Node)pair.getKey()).getCenterX());
                    builder.append(",");
                    builder.append((int) ((Node)pair.getKey()).getCenterY());
                    builder.append(")\"");
                    if(!((HashSet<Node>) pair.getValue()).isEmpty())
                        builder.append(",");
                    Iterator adjacentNodeIterator = ((HashSet<Node>) pair.getValue()).iterator();
                    while(adjacentNodeIterator.hasNext()) {
                        Node node = (Node) adjacentNodeIterator.next();
                        builder.append("\"(");
                        builder.append((int) node.getCenterX());
                        builder.append(",");
                        builder.append((int) node.getCenterY());
                        builder.append(")\"");
                        if(adjacentNodeIterator.hasNext())
                            builder.append(",");
                    }
                    builder.append("\n");
                }

                writer.write(builder.toString());
                writer.close();
                return true;
            }
            catch(IOException e){
                e.printStackTrace();
                return false;
            }

        }
        else if(file.getName().endsWith(".json")){
            try{
                PrintWriter writer = new PrintWriter(file);
                StringBuilder builder = new StringBuilder();

                builder.append("{\n");
                Iterator iterator = nodes.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry pair = (Map.Entry) iterator.next();
                    builder.append("\t\"(");
                    builder.append((int) ((Node)pair.getKey()).getCenterX());
                    builder.append(",");
                    builder.append((int) ((Node)pair.getKey()).getCenterY());
                    builder.append(")\"");
                    builder.append(": [\n");
                    Iterator adjacentNodeIterator = ((HashSet<Node>) pair.getValue()).iterator();
                    while(adjacentNodeIterator.hasNext()){
                        Node node = (Node) adjacentNodeIterator.next();
                        builder.append("\t\t\"(");
                        builder.append((int) node.getCenterX());
                        builder.append(",");
                        builder.append((int) node.getCenterY());
                        builder.append(")\"");
                        if(adjacentNodeIterator.hasNext())
                            builder.append(",");
                        builder.append("\n");
                    }
                    builder.append("\t]");
                    if(iterator.hasNext())
                        builder.append(",\n");
                }
                builder.append("\n}");

                writer.write(builder.toString());
                writer.close();
                return true;
            }
            catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }
        else if(file.getName().endsWith(".tex")){
            try{
                PrintWriter writer = new PrintWriter(file);
                writer.write(latexString(false));
                writer.close();
                return true;
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else if(file.getName().endsWith(".pdf")){
            try{
                File texFile = new File(file.getAbsolutePath().replaceAll(".pdf", ".tex"));
                PrintWriter writer = new PrintWriter(texFile);
                writer.write(latexString(true, texFile.getName().substring(0,texFile.getName().indexOf('.'))));
                writer.close();

                ProcessBuilder pb = new ProcessBuilder().command("pdflatex", texFile.getAbsolutePath()).directory(file.getParentFile()).inheritIO();
                Process createPdfProcess = pb.start();
                try{
                    createPdfProcess.waitFor();
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                    return false;
                }
                pb = new ProcessBuilder().command("rm", texFile.getAbsolutePath(), texFile.getAbsolutePath().replaceAll("tex","log"), texFile.getAbsolutePath().replaceAll("tex","aux")).directory(file.getParentFile()).inheritIO();
                pb.start();
                return true;
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }



    private String latexString(boolean forPDF, String ... args){
        StringBuilder builder = new StringBuilder();
        StringBuilder edges = new StringBuilder();

        builder.append("\\documentclass{article}\n" +
                "\\usepackage[utf8]{inputenc}\n" +
                "\\usepackage{tikz}\n" +
                "\\usepackage{sectsty}\n" +
                "\\usepackage[margin=1in]{geometry}\n" +
                "\\allsectionsfont{\\centering}\n" +
                "\\begin{document}\n");

        if(forPDF){
            builder.append("\\section*{" + args[0] + "}\n");
            builder.append("\\begin{itemize}\n");
            builder.append("\\item Number of nodes: " + nodes.keySet().size() + "\n");
            builder.append("\\item Number of edges: " + this.edges.size() + "\n");
            builder.append("\\item Is Bipartite: " + (bipartition() != null ? "Yes" : "No") + "\n");
            builder.append("\\item Is Connected: " + (isConnected() ? "Yes" : "No") + "\n");
            builder.append("\\end{itemize}");
        }

        builder.append("\\begin{tikzpicture}\n" +
                "\\tikzset{vertex/.style = {shape=circle,draw,minimum size=1.0em}}\n" +
                "\\tikzset{edge/.style = {}}\n");

        Iterator iterator = nodes.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();

            Node currentNode = (Node) pair.getKey();
            builder.append(tikzNode((currentNode)));

            Iterator adjacentNodeIterator = ((HashSet<Node>) pair.getValue()).iterator();
            while(adjacentNodeIterator.hasNext()){
                Node adjacentNode = (Node) adjacentNodeIterator.next();
                edges.append(tikzEdge(currentNode, adjacentNode));
            }
        }

        builder.append(edges.toString());
        builder.append("\\end{tikzpicture}\n");
        builder.append("\\end{document}");
        return builder.toString();
    }

    private String tikzNode(Node node){
        return "\\node[vertex] " +
                "(" + node.hashCode() + ") " +
                "at (" + ((node.getCenterX() / 30)) + "," + (17 - (node.getCenterY() / 30)) + ")" +
                " {};\n";
    }

    private String tikzEdge(Node from, Node to){
        return "\\draw[edge] (" + from.hashCode() + ") to (" + to.hashCode() + ");\n";
    }

    public void clear(){
        nodes.clear();
        edges.clear();
        getChildren().clear();
    }

    public boolean isEmpty(){
        return nodes.isEmpty();
    }

    public void setNodesMovable(boolean movable){
        ArrayList<Node> nodes = new ArrayList<>(this.nodes.keySet());

        for(int i = 0 ; i < nodes.size() ; i++){
            Node node = nodes.get(i);
            if(movable){
                node.setOnMouseDragged(e -> {
                    if(this.getBoundsInParent().contains(e.getX(), e.getY())) {
                        node.setCenterX(e.getX());
                        node.setCenterY(e.getY());
                    }
                    else{
                        System.out.println("Out of bounds (" + e.getX() + "," + e.getY() + ")");
                    }
                });

                node.setOnMouseEntered(e -> {
                    setCursor(Cursor.HAND);
                });

                node.setOnMouseExited(e -> {
                    setCursor(Cursor.DEFAULT);
                });
            }
            else{
                node.setOnMouseDragged(e -> {});
                node.setOnMouseEntered(e -> {});
                node.setOnMouseExited(e -> {});
            }
        }
    }

//    public Stack<Node> shortestPath(Node start, Node end){
//
//        HashSet<Node> visitedNodes = new HashSet<>();
//        Node[] vertices = this.getNodes().toArray(new Node[this.getNodes().size()]);
//        Node[] previousVertices = new Node[vertices.length];
//        double[] shortestDistances = new double[vertices.length];
//
//        for(int i = 0 ; i < shortestDistances.length ; i++){
//            if(vertices[i].equals(start)) shortestDistances[i] = 0;
//            else shortestDistances[i] = Double.MAX_VALUE;
//        }
//
//        while(visitedNodes.size() < vertices.length){
//
//            // Determine the closest node in the unvisited set.
//            int minIndex = 0;
//            double minDist = Double.MAX_VALUE;
//            for(int i = 0 ; i < shortestDistances.length ; i++){
//                if(!visitedNodes.contains(vertices[i]) && shortestDistances[i] < minDist){
//                    minDist = shortestDistances[i];
//                    minIndex = i;
//                }
//            }
//
//            Node current = vertices[minIndex];
//            HashSet<Node> adjacentVertices = nodes.get(current);
//
//            for(int i = 0 ; i < vertices.length ; i++){
//                if(!visitedNodes.contains(vertices[i]) && adjacentVertices.contains(vertices[i])){
//                    double distToStart = minDist + current.distanceTo(vertices[i]);
//                    if(distToStart < shortestDistances[i]){
//                        shortestDistances[i] = distToStart;
//                        previousVertices[i] = current;
//                    }
//                }
//            }
//
//            visitedNodes.add(current);
//        }
//
//        Stack<Node> path = new Stack<>();
//        path.push(end);
//        while(!path.peek().equals(start)){
//            for(int i = 0 ; i < vertices.length ; i++){
//                if(vertices[i].equals(path.peek()) && previousVertices[i] != null){
//                    path.push(previousVertices[i]);
//                }
//            }
//        }
//
//        return path;
//    }

    public Stack<Node> shortestPath(Node start, Node end){

        HashSet<Node> visitedNodes = new HashSet<>();
        Node[] vertices = this.getNodes().toArray(new Node[this.getNodes().size()]);
        Node[] previousVertices = new Node[vertices.length];
        double[] shortestDistances = new double[vertices.length];

        LinkedList<Node> toVisit = new LinkedList<>();
        toVisit.offer(start);

        for(int i = 0 ; i < shortestDistances.length ; i++){
            if(vertices[i].equals(start)) shortestDistances[i] = 0;
            else shortestDistances[i] = Double.MAX_VALUE;
        }

        while(!toVisit.isEmpty()){

            // Determine the closest node in the unvisited set
            double minDist = Double.MAX_VALUE;
            for(int i = 0 ; i < shortestDistances.length ; i++){
                if(!visitedNodes.contains(vertices[i]) && shortestDistances[i] < minDist){
                    minDist = shortestDistances[i];
                }
            }

            Node current = toVisit.poll();
            HashSet<Node> adjacentVertices = nodes.get(current);

            for(int i = 0 ; i < vertices.length ; i++){
                if(!visitedNodes.contains(vertices[i]) && adjacentVertices.contains(vertices[i])){
                    double distToStart = minDist + current.distanceTo(vertices[i]);
                    toVisit.offer(vertices[i]);
                    if(distToStart < shortestDistances[i]){
                        shortestDistances[i] = distToStart;
                        previousVertices[i] = current;
                    }
                }
            }

            visitedNodes.add(current);
        }

        Stack<Node> path = new Stack<>();
        path.push(end);
        while(!path.peek().equals(start)){
            for(int i = 0 ; i < vertices.length ; i++){
                if(vertices[i].equals(path.peek()) && previousVertices[i] != null){
                    path.push(previousVertices[i]);
                }
            }
        }

        return path;
    }



    public HashSet<Node>[] bipartition(){

        if(isEmpty()) return null;

        HashSet<Node> leftSide = new HashSet<>();
        HashSet<Node> rightSide = new HashSet<>();
        HashSet<Node> unvisited = getNodes();
        LinkedList<Node> toVisit = new LinkedList<>();

        while(unvisited.size() > 0){

            Node current;
            if(!toVisit.isEmpty())
                current = toVisit.poll();
            else{
                current = new ArrayList<>(unvisited).get(0);
                unvisited.remove(current);
                if(Math.random() > 0.5)
                    leftSide.add(current);
                else
                    rightSide.add(current);
            }

            for(Node adjacentNode : this.nodes.get(current)){
                if(unvisited.contains(adjacentNode)){
                    unvisited.remove(adjacentNode);
                    toVisit.offer(adjacentNode);
                }
                if(rightSide.contains(current)){
                    if(rightSide.contains(adjacentNode)){
                        return null;
                    }
                    leftSide.add(adjacentNode);
                }
                else {
                    if(leftSide.contains(adjacentNode)){
                        return null;
                    }
                    rightSide.add(adjacentNode);
                }
            }
        }

        return new HashSet[]{leftSide, rightSide};
    }

    public boolean isConnected(){
        if(isEmpty()) return false;

        HashSet<Node> visited = new HashSet<>();
        LinkedList<Node> toVisit = new LinkedList<>();

        Node firstNode = new ArrayList<Node>(this.getNodes()).get(0);
        visited.add(firstNode);
        toVisit.offer(firstNode);

        while(!toVisit.isEmpty()){
            Node current = toVisit.poll();
            for(Node adjacentNode : this.nodes.get(current)){
                if(!visited.contains(adjacentNode)){
                    visited.add(adjacentNode);
                    toVisit.offer(adjacentNode);
                }
            }
        }

        return visited.size() == nodes.keySet().size();
    }

    public String toString(){
        String s = "";
        for(Node node : this.getNodes()){
            s += (node + ": {");
            for(Node connected : nodes.get(node)){
                s += (connected + ", ");
            }
            s += ("}\n");
        }
        return s;
    }
}
