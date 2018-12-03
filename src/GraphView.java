import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Stack;

public class GraphView extends Stage{

    private BorderPane pane;
    private Text directive;
    private Graph graph;
    private Text properties;
    private Button exportButton;
    private Button importButton;
    private Button partitionButton;
    private GraphViewState state;
    private Node firstSelectedNode;
    private ArrayList<javafx.scene.Node> animationShapes;

    public GraphView(){
        this("Graph Pane", false, 0);
    }

    public GraphView(String name, boolean isRandom, int numberOfNodes){

        graph = new Graph();
        pane = new BorderPane();
        directive = new Text();
        animationShapes = new ArrayList<>();
        setState(GraphViewState.NONE);
        pane.setTop(directive);

        // Create the Graph object and implement handler logic.
        graph.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();
            if(state == GraphViewState.ADD_NODES){
                graph.addNode(x, y);
                updateProperties();
            }
            else if(state == GraphViewState.REMOVE_NODES){
                graph.removeNode(x, y);
                updateProperties();
            }
            else if(state == GraphViewState.ADD_EDGE_START){
                firstSelectedNode = graph.getNodeAt(x, y);
                if(firstSelectedNode != null){
                    firstSelectedNode.setFill(Color.GREEN);
                    setState(GraphViewState.ADD_EDGE_END);
                }
                updateProperties();
            }
            else if(state == GraphViewState.ADD_EDGE_END){
                Node secondSelectedNode = graph.getNodeAt(x, y);
                if(secondSelectedNode != null){
                    firstSelectedNode.setFill(Color.BLACK);
                    setState(GraphViewState.ADD_EDGE_START);
                    if(!graph.edgeBetween(firstSelectedNode, secondSelectedNode)){
                        graph.addEdge(firstSelectedNode, secondSelectedNode);
                    }
                }
                updateProperties();
            }
            else if(state == GraphViewState.REMOVE_EDGE_START){
                firstSelectedNode = graph.getNodeAt(x, y);
                if(firstSelectedNode != null){
                    firstSelectedNode.setFill(Color.RED);
                    setState(GraphViewState.REMOVE_EDGE_END);
                }
                updateProperties();
            }
            else if(state == GraphViewState.REMOVE_EDGE_END){
                Node secondSelectedNode = graph.getNodeAt(x, y);
                if(secondSelectedNode != null){
                    firstSelectedNode.setFill(Color.BLACK);
                    setState(GraphViewState.REMOVE_EDGE_START);
                    if(graph.edgeBetween(firstSelectedNode, secondSelectedNode)){
                        graph.removeEdge(firstSelectedNode, secondSelectedNode);
                    }
                }
                updateProperties();
            }
            else if(state == GraphViewState.SHORTEST_PATH_START){
                firstSelectedNode = graph.getNodeAt(x, y);
                if(firstSelectedNode != null){
                    firstSelectedNode.setFill(Color.BLUE);
                    setState(GraphViewState.SHORTEST_PATH_END);
                }
            }
            else if(state == GraphViewState.SHORTEST_PATH_END){
                Node secondSelectedNode = graph.getNodeAt(x, y);
                if(secondSelectedNode != null){
                    Stack<Node> path = graph.shortestPath(firstSelectedNode, secondSelectedNode);
                    clearAnimationShapes();

                    Node previous = path.pop();
                    SequentialTransition sequentialTransition = new SequentialTransition();
                    //sequentialTransition.setCycleCount(Transition.INDEFINITE);
                    while(!path.isEmpty()) {
                        Node current = path.pop();

                        //Define a series of animations that will occur for each step in the path.
                        SequentialTransition innerTransition = new SequentialTransition();

                        //Highlight the next node we are traversing.
                        Timeline nodeHighlightTimeline = new Timeline();
                        nodeHighlightTimeline.setCycleCount(1);
                        nodeHighlightTimeline.setAutoReverse(false);
                        Circle highlightCircle = new Circle(previous.getCenterX(), previous.getCenterY(), 0);
                        highlightCircle.setFill(Color.BLUE);
                        graph.getChildren().add(highlightCircle);
                        animationShapes.add(highlightCircle);
                        KeyFrame kvGrow = new KeyFrame(Duration.millis(500), new KeyValue(highlightCircle.radiusProperty(), 10));
                        nodeHighlightTimeline.getKeyFrames().add(kvGrow);
                        innerTransition.getChildren().add(nodeHighlightTimeline);


                        // Highlight the line over the course of 1 second.
                        Timeline lineHighlightTimeline = new Timeline();
                        lineHighlightTimeline.setCycleCount(1);
                        lineHighlightTimeline.setAutoReverse(false);
                        Line line = new Line(previous.getCenterX(), previous.getCenterY(), previous.getCenterX(), previous.getCenterY());
                        line.setStroke(Color.BLUE);
                        line.setStrokeWidth(3);
                        graph.getChildren().add(line);
                        animationShapes.add(line);
                        KeyValue kvx = new KeyValue(line.endXProperty(), current.getCenterX());
                        KeyValue kvy = new KeyValue(line.endYProperty(), current.getCenterY());
                        KeyFrame kf = new KeyFrame(Duration.millis(1000), kvx, kvy);
                        lineHighlightTimeline.getKeyFrames().add(kf);
                        innerTransition.getChildren().add(lineHighlightTimeline);

                        if(path.isEmpty()){
                            Timeline endHighlightTimeline = new Timeline();
                            endHighlightTimeline.setCycleCount(1);
                            endHighlightTimeline.setAutoReverse(false);
                            Circle endHighlightCircle = new Circle(current.getCenterX(), current.getCenterY(), 0);
                            endHighlightCircle.setFill(Color.BLUE);
                            graph.getChildren().add(endHighlightCircle);
                            animationShapes.add(endHighlightCircle);
                            KeyFrame endKvGrow = new KeyFrame(Duration.millis(500), new KeyValue(endHighlightCircle.radiusProperty(), 10));
                            endHighlightTimeline.getKeyFrames().add(endKvGrow);
                            innerTransition.getChildren().add(endHighlightTimeline);
                        }

                        sequentialTransition.getChildren().add(innerTransition);
                        previous = current;

                    }
                    sequentialTransition.play();
                    setState(GraphViewState.SHORTEST_PATH_START);
                    firstSelectedNode.setFill(Color.BLACK);
                }
            }
        });


        pane.setCenter(graph);


        // Create the HBox to house the properties and export button
        properties = new Text("Number of nodes: " + graph.getNodes().size() + "   Number of edges: " + graph.getEdges().size() + "\n" +
                "Bipartite: N/A" + "   Connected: " + (graph.isConnected() ? "Yes" : "No"));
        exportButton = new Button("Export Graph");
        exportButton.setWrapText(true);
        exportButton.setTextAlignment(TextAlignment.CENTER);
        exportButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export Graph");
            chooser.setInitialFileName(this.getTitle());
            chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/desktop"));
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV", "*.csv"),
                    new FileChooser.ExtensionFilter("JSON", "*.json"),
                    new FileChooser.ExtensionFilter("LaTeX", "*.tex"),
                    new FileChooser.ExtensionFilter("PDF", "*.pdf")
            );
            File saveTo = chooser.showSaveDialog(new Stage());
            if(saveTo != null){
                boolean success = graph.exportGraph(saveTo);
                Alert alert;
                if(success){
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Export Success");
                    alert.setHeaderText("Successful Export");
                    alert.setContentText("You have successfully exported the graph to " + saveTo.getAbsolutePath());
                    alert.showAndWait();
                }
                else {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Export Failure");
                    alert.setHeaderText("Unsuccessful Export");
                    alert.setContentText("An error occured when trying to export the graph to " + saveTo.getAbsolutePath());
                    alert.showAndWait();
                }
            }
        });

        importButton = new Button("Import Graph");
        importButton.setWrapText(true);
        importButton.setTextAlignment(TextAlignment.CENTER);
        importButton.setOnAction(e -> {
            Optional<ButtonType> result = null;
            if(!graph.isEmpty()){
                Alert confimImport = new Alert(Alert.AlertType.CONFIRMATION);
                confimImport.setTitle("Confirm Import");
                confimImport.setHeaderText("Importing a new graph will clear the existing graph.");
                confimImport.setContentText("Press okay to continue or cancel to return to the previous state of the graph.");
                result = confimImport.showAndWait();
            }
            if(graph.isEmpty() || result.get() == ButtonType.OK){
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Import Graph");
                chooser.setInitialFileName(this.getTitle());
                chooser.setInitialDirectory(new File(System.getProperty("user.home") + "/desktop"));
                chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("CSV", "*.csv"),
                        new FileChooser.ExtensionFilter("JSON", "*.json")
                );
                File importFile = chooser.showOpenDialog(new Stage());
                if(importFile != null) {
                    boolean success = graph.importGraph(importFile);
                    Alert alert;
                    if (success) {
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Import Success");
                        alert.setHeaderText("Successful Import");
                        alert.setContentText("You have successfully imported the graph from " + importFile.getAbsolutePath());
                        alert.showAndWait();
                    } else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Import Failure");
                        alert.setHeaderText("Unsuccessful Import");
                        alert.setContentText("An error occured when trying to import the graph to " + importFile.getAbsolutePath() + "." +
                                " Please make sure the file is properly formatted and try again.");
                        alert.showAndWait();
                    }
                    updateProperties();
                }
            }

        });


        partitionButton = new Button("Show Bipartition");
        partitionButton.setDisable(true);
        partitionButton.setWrapText(true);
        partitionButton.setTextAlignment(TextAlignment.CENTER);
        partitionButton.setOnAction(e -> {
            HashSet[] partition = graph.bipartition();
            ArrayList<Node> leftSide = new ArrayList<>(partition[0]);
            ArrayList<Node> rightSide = new ArrayList<>(partition[1]);

            //Move node to the appropriate position.
            Timeline moveNodeTimeline = new Timeline();
            moveNodeTimeline.setCycleCount(1);
            moveNodeTimeline.setAutoReverse(false);

            for(int i = 0 ; i < leftSide.size() ; i++){
                Node currentNode = leftSide.get(i);

                KeyValue kvx = new KeyValue(currentNode.centerXProperty(), 100);
                KeyValue kvy = new KeyValue(currentNode.centerYProperty(), (i + 1) * graph.getHeight() / (leftSide.size() + 1));
                KeyFrame kvGrow = new KeyFrame(Duration.millis(500), kvx, kvy);
                moveNodeTimeline.getKeyFrames().add(kvGrow);
            }

            for(int i = 0 ; i < rightSide.size() ; i++){
                Node currentNode = rightSide.get(i);

                //Move node to the appropriate position.
                KeyValue kvx = new KeyValue(currentNode.centerXProperty(), graph.getWidth() - 100);
                KeyValue kvy = new KeyValue(currentNode.centerYProperty(), (i + 1) * graph.getHeight() / (rightSide.size() + 1));
                KeyFrame kvGrow = new KeyFrame(Duration.millis(500), kvx, kvy);
                moveNodeTimeline.getKeyFrames().add(kvGrow);
            }

            moveNodeTimeline.play();
        });

        HBox bottomBox = new HBox(properties, exportButton, importButton, partitionButton);
        bottomBox.setSpacing(10);
        bottomBox.setAlignment(Pos.CENTER);

        pane.setBottom(bottomBox);



        Scene scene = new Scene(pane, 600, 600);
        setScene(scene);
        setTitle(name);
        show();

        if(isRandom) populateRandomGraph(numberOfNodes);
    }

    public void setState(GraphViewState state){
        if((state == GraphViewState.ADD_EDGE_START || state == GraphViewState.REMOVE_EDGE_START) && firstSelectedNode != null){
            firstSelectedNode.setFill(Color.BLACK);
        }
        if(this.state == GraphViewState.SHORTEST_PATH_START){
            clearAnimationShapes();
            firstSelectedNode.setFill(Color.BLACK);
        }
        this.state = state;
        graph.setNodesMovable(state == GraphViewState.MOVE_NODES);

        switch(state){
            case NONE:
                directive.setText("Please click a button in the toolbar to edit the graph.");
                break;
            case ADD_NODES:
                directive.setText("Click on any free space to add a node to the graph.");
                break;
            case REMOVE_NODES:
                directive.setText("Click on any existing node to remove it.");
                break;
            case ADD_EDGE_START:
                directive.setText("Click on the first node of the edge.");
                break;
            case ADD_EDGE_END:
                directive.setText("Click on the second node of the edge.");
                break;
            case REMOVE_EDGE_START:
                directive.setText("Click on the first node of the edge you would like to remove.");
                break;
            case REMOVE_EDGE_END:
                directive.setText("Click on the second node of the edge you would like to remove.");
                break;
            case MOVE_NODES:
                directive.setText("Click and drag nodes to move them.");
                break;
            case SHORTEST_PATH_START:
                directive.setText("Click on the starting node of the path.");
                break;
            case SHORTEST_PATH_END:
                directive.setText("Click on the ending node of the path.");
        }
    }

    private void clearAnimationShapes(){
        for(int i = 0 ; i < animationShapes.size() ; i++)
            graph.getChildren().remove(animationShapes.get(i));
        animationShapes.clear();
    }

    private void updateProperties(){
        boolean isBipartite = graph.bipartition() != null;
        properties.setText("Number of nodes: " + graph.getNodes().size() + "   Number of edges: " + graph.getEdges().size() +
                "\nBipartite: " + (isBipartite ? "Yes" : "No") + "   Connected: " + (graph.isConnected() ? "Yes" : "No"));
        partitionButton.setDisable(!isBipartite);
    }

    private void populateRandomGraph(int numberOfNodes){
        for(int i = 0 ; i < numberOfNodes ; i++){
            boolean added = false;
            int graphPaneWidth = 500;
            int graphPaneHeight = 500;
            int tries = 0;
            while(!added && tries < 1000){
                tries++;
                added = graph.addNode(Math.random() * graphPaneWidth, Math.random() * graphPaneHeight, 0);
            }
            if(tries > 1000){
                break;
            }
        }
        ArrayList<Node> nodes = new ArrayList<>(graph.getNodes());
        for(int i = 0 ; i < nodes.size() ; i++)
            for(int j = 0 ; j < nodes.size() ; j++)
                if(i!=j && Math.random() < 0.25)
                    graph.addEdge(nodes.get(i), nodes.get(j));
        updateProperties();
    }

}

