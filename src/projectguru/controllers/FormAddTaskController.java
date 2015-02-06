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
import java.time.ZoneId;
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
import projectguru.utils.FormLoader;

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
    private TeamOfficeController controller;

    private Node firstNode = null;
    private Node secondNode = null;

    private final EventHandler<MouseEvent> eventOnClickNext = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            if (secondNode == null) {
                secondNode = FormLoader.getAddMembersNode(allMembers, selectedMembers);
                firstNode = borderPane.getCenter();
            }
            borderPane.setCenter(secondNode);
            btnNext.setDisable(true);
            btnBack.setDisable(false);
        }

    };

    private final EventHandler<MouseEvent> eventOnClickBack = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            borderPane.setCenter(firstNode);
            btnBack.setDisable(true);
            btnNext.setDisable(false);
        }

    };
    private final EventHandler<MouseEvent> eventOnClickHelp = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            FormLoader.showInformationDialog("Напомена", "Како би се успјешно "
                    + "креирао задатак морате унијети сва поља. "
                    + "Додавање чланова је опционо.");
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

            if (strName == null
                    || strDescr == null
                    || startDate == null
                    || endDate == null
                    || strMenHours == null) {
                FormLoader.showInformationDialog("Напомена", "Нисте попунили сва поља!");
                return;
            }
            Integer intMenHours = (new Integer(strMenHours));
            Task tmpTask = new Task(
                    null,
                    strName,
                    intMenHours,
                    Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(dedlineDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
            );

            TaskHandler taskJpa = user.getTaskHandler();
            try {
                if (task != null) {
                    boolean result = taskJpa.addSubtask(task, tmpTask);
                    if (result == true) {
                        if (selectedMembers != null) {
                            Iterator<UserWrapper> itr = selectedMembers.iterator();
                            while (itr.hasNext()) {
                                taskJpa.addMember(
                                        tmpTask,
                                        itr.next().getUser()
                                );
                            }
                        }
                    }
                }else {
                    user.getProjectHandler().setRootTask(project, tmpTask);
                }
                controller.addNodeToTree(tmpTask);
                
                Stage stage = (Stage) btnFinish.getScene().getWindow();
                controller.loadTaskTree(project);
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
    @FXML
    private Button btnHelp;
    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnNext.setOnMouseClicked(eventOnClickNext);
        btnBack.setOnMouseClicked(eventOnClickBack);
        btnFinish.setOnMouseClicked(eventOnClickFinish);
        btnHelp.setOnMouseClicked(eventOnClickHelp);
    }

    public void setUser(LoggedUser user) {
        this.user = user;
        if (task != null || user != null) {
            defineObservableList();
        }
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setTask(Task task) {
        this.task = task;
        if (task != null || user != null) {
            defineObservableList();
        }
    }

    private void defineObservableList() {
        allMembers = FXCollections.observableArrayList(
                user.getTaskHandler().getAllMembers(task)
                .stream()
                .map((member) -> new UserWrapper(member))
                .collect(Collectors.toList()));
        selectedMembers = FXCollections.observableArrayList();
    }

    public void setController(TeamOfficeController controller) {
        this.controller = controller;
    }

}
