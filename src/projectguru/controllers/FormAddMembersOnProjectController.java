/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import projectguru.entities.Project;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.exceptions.StoringException;
import projectguru.utils.FormLoader;

/**
 * FXML Controller class
 *
 * @author Marko
 */
public class FormAddMembersOnProjectController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnHelp;

    private ObservableList<TeamOfficeController.UserWrapper> allMembers;
    private ObservableList<TeamOfficeController.UserWrapper> selectedMembers;

    private LoggedUser user;
    private Project project;
    private TeamOfficeController controller;

    private final EventHandler<MouseEvent> eventOnClickHelp = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            FormLoader.showInformationDialog("Помоћ", "Све чланове које желите да додате"
                    + "превуците у десну листу");
        }
    };
    private final EventHandler<MouseEvent> eventOnClickAdd = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            ProjectHandler projectJpa = user.getProjectHandler();
            if (selectedMembers != null) {
                Iterator<TeamOfficeController.UserWrapper> itr = selectedMembers.iterator();
                while (itr.hasNext()) {
                    try {
                        projectJpa.addMember(
                                project,
                                itr.next().getUser()
                        );
                    } catch (StoringException ex) {
                        FormLoader.showInformationDialog("StoringException", ex.getLocalizedMessage());
                        Logger.getLogger(FormAddMembersOnProjectController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                Stage stage = (Stage) btnAdd.getScene().getWindow();
                controller.loadMembers(project);
                FormLoader.showInformationDialog("Упозорење", "Чланови који су на пројекту не могу"
                        + " да се уклоне са пројекта");
                stage.close();
            }
        }
    };

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnHelp.setOnMouseClicked(eventOnClickHelp);
        btnAdd.setOnMouseClicked(eventOnClickAdd);
    }

    public void setUser(LoggedUser user) {
        this.user = user;
        if (project != null || user != null) {
            init();
        }
    }

    public void setProject(Project project) {
        this.project = project;
        if (project != null || user != null) {
            init();
        }
    }

    public void setController(TeamOfficeController controller) {
        this.controller = controller;
    }

    private void init() {

        allMembers = FXCollections.observableArrayList(
                user.getUserHandler().getAllUsers()
                .stream()
                .map((member) -> new TeamOfficeController.UserWrapper(member))
                .collect(Collectors.toList())
        );

        allMembers.removeAll(FXCollections.observableArrayList(
                user.getProjectHandler().getAllMembers(project)
                .stream()
                .map((member) -> new TeamOfficeController.UserWrapper(member))
                .collect(Collectors.toList()))
        );
        
        selectedMembers = FXCollections.observableArrayList();
        borderPane.setCenter(FormLoader.getAddMembersNode(allMembers, selectedMembers));
    }
}
