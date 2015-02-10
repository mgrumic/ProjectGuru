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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import projectguru.controllers.TeamOfficeController.DocumentWrapper;
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
import projectguru.utils.FormLoader;

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
    private ChoiceBox<DocumentWrapper> chooseBoxRevisionOn;
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
    @FXML
    private CheckBox checkBoxRev;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textBoxDesc.setDisable(false);
        textBoxName.setDisable(false);
        chooseBoxRevisionOn.setDisable(true);

    }

    public void loadDocum() {
        List<Document> listAll = project.getDocumentList();
        List<TeamOfficeController.DocumentWrapper> dwList = new ArrayList<TeamOfficeController.DocumentWrapper>();
        for (int i = 0; i < listAll.size(); i++) {
            dwList.add(new TeamOfficeController.DocumentWrapper(listAll.get(i)));
        }

        ObservableList<TeamOfficeController.DocumentWrapper> docw = FXCollections.observableArrayList(dwList);
        chooseBoxRevisionOn.setItems(docw);
        chooseBoxRevisionOn.getSelectionModel().selectFirst();
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
        try {
            if (!checkBoxRev.isSelected()) {
                if (textBoxDesc.getText() != "" && textBoxName.getText() != "" && file != null) {
                    byte[] fajl = Files.readAllBytes(Paths.get(file.getPath()));
                    String desc = this.textBoxDesc.getText();
                    String name = this.textBoxName.getText();
                    Calendar cal = Calendar.getInstance();
                    Date date = cal.getTime();

                    Document d = new Document(null, name, date, desc);
                    DocumentRevision drev = new DocumentRevision(null, 1, fajl, date, desc);
                    //ProjectHandler prJpa = user.getProjectHandler(); 
                    //prJpa.addDocument(project, d);
                    //treba mi metoda koja vraca zadnji dokument
                    DocumentHandler docJpa = user.getDocumentHandler();

                    docJpa.addDocument(project, d);
                    docJpa.addRevision(d, drev);
                    Stage stage = (Stage) btnSave.getScene().getWindow();
                    stage.close();
                } else {
                    FormLoader.showInformationDialog("Напомена", "Морате попунити сва поља!");
                }

            } else {
                if (file != null) {
                    TeamOfficeController.DocumentWrapper dw = chooseBoxRevisionOn.getItems().get(chooseBoxRevisionOn.getSelectionModel().getSelectedIndex());

                    byte[] fajl = Files.readAllBytes(Paths.get(file.getPath()));
                    //String desc = this.textBoxDesc.getText();
                    Date date = new Date();
                    Document doc = dw.getDocument();
                    DocumentRevision drev = new DocumentRevision(null, 1, fajl, date, "");
                    DocumentHandler docJpa = user.getDocumentHandler();
                    docJpa.addRevision(doc, drev);
                    Stage stage = (Stage) btnSave.getScene().getWindow();
                    stage.close();
                } else {
                    FormLoader.showInformationDialog("Напомена", "Морате попунити сва поља!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (EntityDoesNotExistException e) {
            e.printStackTrace();
        } catch (InsuficientPrivilegesException i) {
            i.printStackTrace();
        } catch (StoringException s) {
            s.printStackTrace();
        }

    }

    @FXML
    private void checkBoxRevPressed(ActionEvent event) {
        if (checkBoxRev.isSelected()) {
            textBoxDesc.setDisable(true);
            textBoxName.setDisable(true);
            chooseBoxRevisionOn.setDisable(false);

        } else {
            textBoxDesc.setDisable(false);
            textBoxName.setDisable(false);
            chooseBoxRevisionOn.setDisable(true);
        }
    }

    @FXML
    private void btnCancelPressed(ActionEvent event) {
        if (FormLoader.showConfirmationDialog("Да ли сте сигурни?", "Да ли сте сигурни да желите изаћи?")) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }
    }
}
