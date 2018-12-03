import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.Scene;

import java.util.ArrayList;

public class GraphAnimationToolbar extends Application{

    ArrayList<GraphView> graphViews = new ArrayList<>();
    private static boolean creatingNewGraph;

    public void start(Stage primaryStage){


        Button addNodesButton = new Button("Add Nodes");
        addNodesButton.setOnMouseClicked(e -> {
            for(GraphView view : graphViews){
                view.setState(GraphViewState.ADD_NODES);
            }
        });


        Button removeNodesButton = new Button("Remove Nodes");
        removeNodesButton.setOnMouseClicked(e -> {
            for(GraphView view : graphViews){
                view.setState(GraphViewState.REMOVE_NODES);
            }
        });


        Button addEdgesButton = new Button("Add Edges");
        addEdgesButton.setOnMouseClicked(e -> {
            for(GraphView view : graphViews){
                view.setState(GraphViewState.ADD_EDGE_START);
            }
        });


        Button removeEdgesButton = new Button("Remove Edges");
        removeEdgesButton.setOnMouseClicked(e -> {
            for(GraphView view : graphViews){
                view.setState(GraphViewState.REMOVE_EDGE_START);
            }
        });

        Button moveNodesButton = new Button("Move Nodes");
        moveNodesButton.setOnMouseClicked(e -> {
            for(GraphView view : graphViews){
                view.setState(GraphViewState.MOVE_NODES);
            }
        });

        Button shortestPathButton = new Button("Shortest Path");
        shortestPathButton.setOnMouseClicked(e -> {
            for(GraphView view : graphViews){
                view.setState(GraphViewState.SHORTEST_PATH_START);
            }
        });


        Button newGraphButton = new Button("New Graph");
        newGraphButton.setOnMouseClicked(e -> {
            if(!creatingNewGraph) {
                creatingNewGraph = true;
                new CreateGraphView(graphViews);
            }
        });

        ToolBar pane = new ToolBar(
                addNodesButton,
                new Separator(Orientation.VERTICAL),
                removeNodesButton,
                new Separator(Orientation.VERTICAL),
                addEdgesButton,
                new Separator(Orientation.VERTICAL),
                removeEdgesButton,
                new Separator(Orientation.VERTICAL),
                shortestPathButton,
                new Separator(Orientation.VERTICAL),
                moveNodesButton,
                new Separator(Orientation.VERTICAL),
                newGraphButton);

        Scene scene = new Scene(pane);

        primaryStage.setTitle("Graph Animations Toolbar");
        primaryStage.setScene(scene);
        primaryStage.setY(0);
        primaryStage.setOnCloseRequest( e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    public static void setCreatingNewGraph(boolean isCreatingNewGraph){
        creatingNewGraph = isCreatingNewGraph;
    }

    public static void main(String[] args){
        launch(args);
    }
}