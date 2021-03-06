/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import projectguru.controllers.TeamOfficeController.DocumentRevisionWrapper;
import projectguru.entities.Document;
import projectguru.entities.Project;
import projectguru.handlers.DocumentHandler;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.utils.FormLoader;
import projectguru.controllers.TeamOfficeController.DocumentWrapper;
import projectguru.entities.DocumentRevision;

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
    private ListView<DocumentRevisionWrapper> listViewRevision;
    @FXML
    private Button buttonOpen;
    @FXML
    private Button ButtonAdd;
    @FXML
    private ChoiceBox<DocumentWrapper> choiceBoxDocum;
    private Label labelDate;
    @FXML
    private Label labesDescription;

//  
//    public FormDocumentationController(Project project,LoggedUser user){
//        this.project = project;
//        this.user = user;
//    }
    ObservableList<DocumentRevisionWrapper> docw;
    @FXML
    private Label labelDescRevision;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //loadDocum();

    }

    public void loadDocum() {
        buttonOpen.setDisable(false);
//        try {
//            project = user.getProjectHandler().getUpdatedProject(project);
//        } catch (EntityDoesNotExistException ex) {
//            Logger.getLogger(FormDocumentationController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        List<Document> listAll = new ArrayList<>();
        try {
            listAll = user.getProjectHandler().getAllDocuments(project);
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(FormDocumentationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<DocumentWrapper> dwList = new ArrayList<DocumentWrapper>();
        for (int i = 0; i < listAll.size(); i++) {
            dwList.add(new DocumentWrapper(listAll.get(i)));
        }

        ObservableList<DocumentWrapper> docw = FXCollections.observableArrayList(dwList);
        choiceBoxDocum.setItems(docw);
        choiceBoxDocum.getSelectionModel().selectFirst();
        loadRevision(choiceBoxDocum.getSelectionModel().getSelectedItem());
        DocumentWrapper item = choiceBoxDocum.getSelectionModel().getSelectedItem();
        if (item != null) {
            this.labesDescription.setText(item.getDocument().getDescription());
        } else {
            this.labesDescription.setText("");
        }
        choiceBoxDocum.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DocumentWrapper>() {

            @Override
            public void changed(ObservableValue<? extends DocumentWrapper> observable, DocumentWrapper oldValue, DocumentWrapper newValue) {
                loadRevision(newValue);
            }
        });
        if (listAll.size() == 0) {
            FormLoader.showInformationDialog("Напомена", "Немате ни један додан документ !");
            buttonOpen.setDisable(true);

        }
    }

    public void loadRevision(DocumentWrapper docwr) {

        if (docwr == null) {
            listViewRevision.setItems(FXCollections.observableArrayList());
            return;
        }

        if (docwr.getDocument() == null) {
            try {
                throw new Error();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        Document doc = docwr.getDocument();

        labesDescription.setText(doc.getDescription());

        List<DocumentRevision> listAll = doc.getDocumentRevisionList();
        List<DocumentRevisionWrapper> dwList = new ArrayList<DocumentRevisionWrapper>();
        for (int i = 0; i < listAll.size(); i++) {
            dwList.add(new DocumentRevisionWrapper(listAll.get(i)));
        }

        docw = FXCollections.observableArrayList(dwList);
        listViewRevision.setItems(docw);
        listViewRevision.getSelectionModel().selectFirst();
        if (listAll.size() > 0) {
            this.labelDescRevision.setText(listViewRevision.getSelectionModel().getSelectedItem().getDocument().getDescription());
        }
        listViewRevision.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DocumentRevisionWrapper>() {

            @Override
            public void changed(ObservableValue<? extends DocumentRevisionWrapper> observable, DocumentRevisionWrapper oldValue, DocumentRevisionWrapper newValue) {
                if (newValue != null) {
                    labelDescRevision.setText(newValue.getDocument().getDescription());
                } else {
                    labelDescRevision.setText("");
                }
            }
        });

    }

    @FXML
    void buttonAddDocPressed(ActionEvent event) {
        if (!user.getProjectHandler().checkMemberPrivileges(project)) {
            FormLoader.showInformationDialog("Напомена", "Немате привилегију за додавање документа!");

        } else {
            try {
                FormLoader.loadFormAddDocumentation(project, user);
                loadDocum();
            } catch (IOException ex) {
                Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setUser(LoggedUser user) {
        this.user = user;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @FXML
    private void openFile(ActionEvent event) {
        DocumentRevisionWrapper drw = listViewRevision.getSelectionModel().getSelectedItem();

        if (drw == null) {
            return;
        }

        byte[] file = drw.getDocument().getBinData();
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
//        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
//        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("Microsoft Word Documents", ".docx");
//        FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter("PDF Documents", ".pdf");
//        FileChooser.ExtensionFilter extFilter4 = new FileChooser.ExtensionFilter("Microsoft Excel Documents", ".xlsx");
//
//        fileChooser.getExtensionFilters().add(extFilter);
//        fileChooser.getExtensionFilters().add(extFilter2);
//        fileChooser.getExtensionFilters().add(extFilter3);
//        fileChooser.getExtensionFilters().add(extFilter4);
        //Show save file dialog
        fileChooser.setInitialFileName(drw.getDocument().getFileName());
        File fileSave = fileChooser.showSaveDialog(null);

        if (fileSave != null) {
            SaveFile(fileSave.getPath(), file);
        }
    }

    private void SaveFile(String content, byte[] bFile) {
        try {

            //convert array of bytes into file
            FileOutputStream fileOuputStream
                    = new FileOutputStream(content);
            fileOuputStream.write(bFile);
            fileOuputStream.close();

            //   System.out.println("Done");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
