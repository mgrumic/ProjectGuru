/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import projectguru.entities.Project;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;

/**
 * FXML Controller class
 *
 * @author win7
 */
public class FormAddDocumentationController implements Initializable {
    
    protected LoggedUser user;
    protected Project project;
    protected final FileChooser fileChooser = new FileChooser();
    
    @FXML
    private AnchorPane anchor;
    @FXML
    private Label label;
    @FXML
    private ChoiceBox<?> chooseBoxRevisionOn;
    @FXML
    private TextField textBoxName;
    @FXML
    private TextField textBoxDocPath;
    @FXML
    private Button btnChooseDoc;
    @FXML
    private TextArea textBoxDesc;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setUser(LoggedUser user) {
        this.user = user;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @FXML
    private void btnChooseDocPressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(null);
        this.textBoxDocPath.setText(file.getPath());
    }
}
