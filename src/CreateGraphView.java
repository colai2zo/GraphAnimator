import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.util.ArrayList;

public class CreateGraphView extends Stage {

    public CreateGraphView(ArrayList<GraphView> currentGraphs){
        setTitle("New Graph Options");

        TextField name = new TextField("Graph " + Integer.toString(currentGraphs.size() + 1));
        Text nameText = new Text("Name");
        nameText.setTextAlignment(TextAlignment.CENTER);
        HBox nameRow = new HBox(nameText, name);
        nameRow.setSpacing(10);
        nameRow.setPadding(new Insets(10,10,10,10));

        CheckBox isRandomToggle = new CheckBox();
        Text randomGraphText = new Text("Random Graph");
        HBox randomGraphRow = new HBox(randomGraphText, isRandomToggle);
        randomGraphRow.setSpacing(10);
        randomGraphRow.setPadding(new Insets(10,10,10,10));

        Spinner<Integer> numberOfNodesSpinner = new Spinner<>(0,50,0);
        numberOfNodesSpinner.disableProperty().bind(isRandomToggle.selectedProperty().not());
        Text numberOfNodesText = new Text("Number of Nodes");
        HBox numberOfNodesRow = new HBox(numberOfNodesText, numberOfNodesSpinner);
        numberOfNodesRow.setSpacing(10);
        numberOfNodesRow.setPadding(new Insets(10,10,10,10));

        Button generateGraphButton = new Button("Generate Graph");
        generateGraphButton.setPadding(new Insets(10,10,10,10));
        generateGraphButton.setOnMouseClicked(e -> {
            currentGraphs.add(new GraphView(name.getText(), isRandomToggle.isSelected(), numberOfNodesSpinner.getValue()));
            GraphAnimationToolbar.setCreatingNewGraph(false);
            close();
        });
        generateGraphButton.setTextAlignment(TextAlignment.CENTER);

        VBox optionPane = new VBox(nameRow, randomGraphRow, numberOfNodesRow, generateGraphButton);
        optionPane.setSpacing(10);
        Scene scene = new Scene(optionPane);
        setScene(scene);
        show();

        setOnCloseRequest( e -> {
            GraphAnimationToolbar.setCreatingNewGraph(false);
        });
    }

}
