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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import projectguru.handlers.LoggedUser;

/**
 * FXML Controller class
 *
 * @author ZM
 */
public class LoginController implements Initializable {

    @FXML
    private TextField tfUsername;
    
    @FXML
    private TextField tfPassword;
    
    @FXML
    private Button btnLogin;
    
    @FXML
    private Button btnExit;
    
    @FXML
    private Label lblWarning;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lblWarning.setVisible(false);
        
    }    
    
}
