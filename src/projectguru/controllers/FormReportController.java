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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import projectguru.jpa.handlers.JpaReportHandler;

/**
 * FXML Controller class
 *
 * @author medlan
 */
public class FormReportController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button generateReportButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    void onGenerateReportButtonClick(ActionEvent evt){
        new JpaReportHandler().generateReport("select username, password, firstname, lastname from user");
    }
    
}
