/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer3d;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
    @FXML private BorderPane ap;
    
    
        @FXML
    private void handleMenuItemOpenAction(ActionEvent event) {
        System.out.println("Open");
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setInitialDirectory(new File("data"));
        //fileChooser.setInitialFileName("myfile.txt");
        fileChooser.getExtensionFilters().addAll(
         new FileChooser.ExtensionFilter("Obj Files", "*.obj")
        ,new FileChooser.ExtensionFilter("csv Files", "*.csv")
        );
        
        Stage stage = (Stage) ap.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null && selectedFile.exists()){
            System.out.println(selectedFile.getPath());
        }else{
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
        Stage stage = (Stage) ap.getScene().getWindow();
        stage.close();

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

}
