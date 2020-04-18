/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer3d;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 *
 * @author leeyo
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private BorderPane ap;
    @FXML
    private TreeView modelTreeView;

    @FXML
    private void handleMenuItemOpenAction(ActionEvent event) {
        System.out.println("Open");
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setInitialDirectory(new File("data"));
        //fileChooser.setInitialFileName("myfile.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Obj Files", "*.obj"),
                new FileChooser.ExtensionFilter("csv Files", "*.csv")
        );

        Stage stage = (Stage) ap.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null && selectedFile.exists()) {
            System.out.println(selectedFile.getPath());
        } else {
            System.out.println("File could not be opened.");
        }
    }

    @FXML
    private void handleMenuItemImportAction(ActionEvent event) {
        System.out.println("Import");

    }

    @FXML
    private void handleMenuItemSaveAction(ActionEvent event) {
        System.out.println("Save");

    }

    @FXML
    private void handleMenuItemCloseAction(ActionEvent event) {
        System.out.println("Close");
        Platform.exit();
    }

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initModelTreeView();
    }

    private void initModelTreeView() {
        //create root
        TreeItem<String> rootTree = new TreeItem<>("Model");
        //root.setExpanded(true);

        //create child
        TreeItem<String> geomChild = new TreeItem<>("Geometry");
        rootTree.getChildren().add(geomChild);
        geomChild.setExpanded(false);

        TreeItem<String> geomChildNodes = new TreeItem<>("Nodes");
        geomChild.getChildren().add(geomChildNodes);
        geomChild.setExpanded(false);
        TreeItem<String> geomChildElem = new TreeItem<>("Elements");
        geomChild.getChildren().add(geomChildElem);
        geomChild.setExpanded(false);

        modelTreeView.setRoot(rootTree);
        
        
        //mouseevent
        modelTreeView.setOnMouseClicked (new EventHandler<MouseEvent>()
{
    @Override
    public void handle(MouseEvent mouseEvent)
    {            
        if (mouseEvent.getClickCount() == 2) {
                TreeItem item = (TreeItem) modelTreeView.getSelectionModel().getSelectedItem();
                if (item.isLeaf()){
                    System.out.println("Selected Text : " + item.getValue());
                }
            }
        }
    }

    );
        
    }

}
