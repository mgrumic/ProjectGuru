/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author marko
 */
public class FormActivitiesController implements Initializable {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAddActivity;

    @FXML
    private Button btnDeleteActivity;

    @FXML
    private Button btnEditActivity;

    @FXML
    void btnAddActivity_OnAction(ActionEvent event) {

    }

    @FXML
    void btnEditActivity_OnAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteActivity_OnAction(ActionEvent event) {

    }
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        assert btnAddActivity != null : "fx:id=\"btnAddActivity\" was not injected: check your FXML file 'FormActivities.fxml'.";
        assert btnDeleteActivity != null : "fx:id=\"btnDeleteActivity\" was not injected: check your FXML file 'FormActivities.fxml'.";
        assert btnEditActivity != null : "fx:id=\"btnEditActivity\" was not injected: check your FXML file 'FormActivities.fxml'.";


    }    
    
}
