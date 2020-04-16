/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer3d;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author leeyo
 */
public class FXMLDocumentController implements Initializable {
    
        @FXML
    private void handleMenuItemOpenAction(ActionEvent event) {
        System.out.println("Open");

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

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
