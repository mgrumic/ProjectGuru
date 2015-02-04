/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import projectguru.controllers.TeamOfficeController.UserWrapper;
import projectguru.entities.Project;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.jpa.handlers.JpaProjectHandler;
import projectguru.utils.FormLoader;

/**
 * FXML Controller class
 *
 * @author Marko
 */
public class FormAddProjectController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Button btnFinish;
    @FXML
    private Button btnNext;
    @FXML
    private TextField name;
    @FXML
    private TextField budget;
    @FXML
    private TextField description;
    @FXML
    private DatePicker start;
    @FXML
    private DatePicker ends;
    @FXML
    private Label label;

    private LoggedUser user;

    private ObservableList<UserWrapper> allMembers;
    private ObservableList<UserWrapper> selectedMembers;
    private TeamOfficeController controller;
    
    private final EventHandler<MouseEvent> eventOnClickNext = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            Node view;
            try {

                allMembers = FXCollections.observableArrayList(
                        user.getUserHandler().getAllUsers()
                        .stream()
                        .map((member) -> new UserWrapper(member))
                        .collect(Collectors.toList())
                );

                String strName = name.getText();
                String strDescr = description.getText();
                LocalDate startDate = start.getValue();
                LocalDate endDate = ends.getValue();
                String strBudget = budget.getText();

                if (strName == null || strDescr == null || startDate == null || endDate == null || strBudget == null) {
                    label.setText("Нисте попунили сва поља");
                    label.setTextFill(Color.web("#ff0000"));
                    return;
                }

                selectedMembers = FXCollections.observableArrayList();
                FXMLLoader fxmlLoader;
                fxmlLoader = new FXMLLoader(getClass().getResource("/projectguru/fxml/FormAddMembers.fxml"));
                fxmlLoader.setController(new FormAddMembersController(allMembers, selectedMembers));
                view = fxmlLoader.load();
                borderPane.setCenter(view);
            } catch (IOException ex) {
                Logger.getLogger(FormAddMembersController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    };

    private final EventHandler<MouseEvent> eventOnClickFinish = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            String strName = name.getText();
            String strDescr = description.getText();
            LocalDate startDate = start.getValue();
            LocalDate endDate = ends.getValue();
            String strBudget = budget.getText();

            if (strName == null || strDescr == null || startDate == null || endDate == null || strBudget == null) {
                label.setText("Нисте попунили сва поља");
                label.setTextFill(Color.web("#ff0000"));
                return;
            }
            Long lon = (new Long(strBudget));
            Project project = new Project(
                    null,
                    strName,
                    BigDecimal.valueOf(lon.longValue()),
                    new Date(startDate.toEpochDay()),
                    new Date(endDate.toEpochDay()),
                    strDescr
            );

            try {
                ProjectHandler projectJpa = user.getProjectHandler();
                projectJpa.createProject(project);
                if (selectedMembers != null) {
                    Iterator<UserWrapper> itr = selectedMembers.iterator();
                    while (itr.hasNext()) {

                        projectJpa.addMember(
                                project,
                                itr.next().getUser()
                        );
                    }
                }
                Stage stage = (Stage) btnFinish.getScene().getWindow();
                controller.loadProjects();
                stage.close();
            } catch (InsuficientPrivilegesException ex) {
                FormLoader.showInformationDialog("Напомена", "Немате довољно привилегија !");
            } catch (StoringException ex) {
                FormLoader.showInformationDialog("Напомена", "Грешка са базом !");
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
        btnNext.setOnMouseClicked(eventOnClickNext);
        btnFinish.setOnMouseClicked(eventOnClickFinish);
        allMembers = null;
        selectedMembers = null;
    }

    public void setUser(LoggedUser user) {
        this.user = user;
    }
    public void setController(TeamOfficeController controller) {
        this.controller = controller;
    }
}
