/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
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
import javafx.stage.Stage;
import projectguru.entities.Document;
import projectguru.entities.DocumentRevision;
import projectguru.entities.Project;
import projectguru.entities.User;
import projectguru.handlers.DocumentHandler;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;

/**
 * FXML Controller class
 *
 * @author win7
 */
public class FormAddDocumentationController implements Initializable {
    
    protected LoggedUser user;
    protected Project project;
    protected final FileChooser fileChooser = new FileChooser();
    private FormDocumentationController controller;
    private File file;
    
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
        file = fileChooser.showOpenDialog(null);
        this.textBoxDocPath.setText(file.getPath());
     
    }

    @FXML
    private void btnSavePressed(ActionEvent event) {
      try{
      byte[] fajl = Files.readAllBytes(Paths.get(file.getPath())); 
      String desc = this.textBoxDesc.getText();
      String name = this.textBoxName.getText();
      Date date = new Date();
      
      Document d = new Document(null, name, date);
      DocumentRevision drev = new DocumentRevision(null,1,fajl,date);
      ProjectHandler prJpa = user.getProjectHandler(); 
      prJpa.addDocument(project, d);
     // Document d = prJpa.g treba mi metoda koja vraca zadnji dokument
      DocumentHandler docJpa = user.getDocumentHandler();
      docJpa.addRevision(d,drev);
      
      }catch(IOException e){ e.printStackTrace();}
      catch(EntityDoesNotExistException e){e.printStackTrace();}
      catch(InsuficientPrivilegesException i){i.printStackTrace();}
      catch(StoringException s){s.printStackTrace();}
       Stage stage = (Stage) btnSave.getScene().getWindow();
                //controller.loadDocum;
                stage.close();
    
    }
}
