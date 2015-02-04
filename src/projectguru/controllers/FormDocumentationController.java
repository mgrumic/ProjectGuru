/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import projectguru.entities.Project;
import projectguru.handlers.LoggedUser;
import projectguru.utils.FormLoader;

/**
 * FXML Controller class
 *
 * @author win7
 */
public class FormDocumentationController implements Initializable {

    /**
     * Initializes the controller class.
     */
    LoggedUser user;
    Project project;
    @FXML
    private AnchorPane AnchorDocum;
    @FXML
    private Label labDocum;
    @FXML
    private ListView<?> listViewRevision;
    @FXML
    private Button buttonOpen;
    @FXML
    private Button ButtonAdd;
    @FXML
    private ChoiceBox<?> choiceBoxDocum;
    @FXML
    private Label labelDate;
    @FXML
    private Label labesDescription;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    void buttonAddDocPressed(ActionEvent event) {
        if (user.getUser().getAppPrivileges() != 4) {
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(VBoxBuilder.create().children(new Text("Немате привилегију за додавање документа!")).alignment(Pos.CENTER).padding(new Insets(35)).build()));
            dialogStage.show();
        } else {
            try {
                FormLoader.loadFormAddDocumentation(project, user);
            } catch (IOException ex) {
                Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) { e.printStackTrace();
            }

        }
    }

    public void setUser(LoggedUser user) {
        this.user = user;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
