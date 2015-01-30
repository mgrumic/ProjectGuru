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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author marko
 */
public class FormActivitiesController implements Initializable {


    @FXML
    private TextField txtRemark;

    @FXML
    private TableView<?> tblActivities;

    @FXML
    private TextField txtName;

    @FXML
    private TextArea txtDescription;

    @FXML
    private Button btnNewActivity;

    @FXML
    private CheckBox chbOnlyMyActivity;

    @FXML
    private Button btnReject;

    @FXML
    private Button btnDeleteActivity;

    @FXML
    private Button btnSaveChanges;

    @FXML
    private TextField txtTaskName;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField txtCreator;

    @FXML
    private CheckBox chbOnlyThisTask;

    @FXML
    void btnSaveChanges_OnAction(ActionEvent event) {

    }

    @FXML
    void btnReject_OnAction(ActionEvent event) {

    }

    @FXML
    void btnNewActivity_OnAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteActivity_OnAction(ActionEvent event) {

    }

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        


    }    
    
}
