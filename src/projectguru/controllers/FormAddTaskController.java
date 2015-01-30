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
import javafx.stage.Stage;
import projectguru.controllers.TeamOfficeController.UserWrapper;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.TaskHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;

/**
 * FXML Controller class
 *
 * @author win7
 */
public class FormAddTaskController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button btnFinish;
    @FXML
    private Button btnNext;
    @FXML
    private TextField name;
    @FXML
    private TextField menHours;
    @FXML
    private TextField description;
    @FXML
    private DatePicker start;
    @FXML
    private DatePicker ends;
    @FXML
    private DatePicker dedline;
    @FXML
    private Label label;

    private LoggedUser user;
    private Project project;
    private Task task;

    private ObservableList<TeamOfficeController.UserWrapper> allMembers;
    private ObservableList<TeamOfficeController.UserWrapper> selectedMembers;

    private final EventHandler<MouseEvent> eventOnClickNext = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            Node view;
            try {

                allMembers = FXCollections.observableArrayList(
                        user.getProjectHandler().getAllMembers(project)
                        .stream()
                        .map((member) -> new UserWrapper(member))
                        .collect(Collectors.toList()));

                String strName = name.getText();
                String strDescr = description.getText();
                LocalDate startDate = start.getValue();
                LocalDate endDate = ends.getValue();
                LocalDate dedlineDate = dedline.getValue();
                String strMenHours = menHours.getText();

                if (strName == null || strDescr == null || startDate == null || endDate == null || strMenHours == null || dedlineDate == null) {
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
            LocalDate dedlineDate = dedline.getValue();
            String strMenHours = menHours.getText();

            if (strName == null || strDescr == null || startDate == null || endDate == null || strMenHours == null) {
                label.setText("Нисте попунили сва поља");
                label.setTextFill(Color.web("#ff0000"));
                return;
            }
            Integer intMenHours = (new Integer(strMenHours));
            Task tmpTask = new Task(
                    null,
                    strName,
                    intMenHours,
                    new Date(startDate.toEpochDay()),
                    new Date(endDate.toEpochDay()),
                    new Date(dedlineDate.toEpochDay())
            );

            TaskHandler taskJpa = user.getTaskHandler();
            try {
                taskJpa.addSubtask(task, tmpTask);
                if (selectedMembers != null) {
                    Iterator<UserWrapper> itr = selectedMembers.iterator();
                    while (itr.hasNext()) {
                        taskJpa.addMember(
                                tmpTask, 
                                itr.next().getUser()
                        );
                    }
                }
                Stage stage = (Stage) btnFinish.getScene().getWindow();
                stage.close();
            } catch (EntityDoesNotExistException ex) {
                Logger.getLogger(FormAddTaskController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (StoringException ex) {
                Logger.getLogger(FormAddTaskController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InsuficientPrivilegesException ex) {
                Logger.getLogger(FormAddTaskController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    };

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

    public void setProject(Project project) {
        this.project = project;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
