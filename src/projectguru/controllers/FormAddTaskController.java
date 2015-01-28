/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author win7
 */
public class FormAddTaskController implements Initializable {
    @FXML
    private AnchorPane page1;
    @FXML
    private TextArea description;
    @FXML
    private TextField mensHours;
    @FXML
    private DatePicker end;
    @FXML
    private DatePicker start;
    @FXML
    private TextField name;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonOK;
    @FXML
    private AnchorPane page2;
    @FXML
    private ListView<?> listViewSelectedMembers;
    @FXML
    private ListView<?> listViewAllMembers;
    @FXML
    private Button buttonAllToSel;
    @FXML
    private Button buttonOneToSel;
    @FXML
    private Button buttonAllFromSel;
    @FXML
    private Button buttonOneFromSel;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Button buttonOk;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
