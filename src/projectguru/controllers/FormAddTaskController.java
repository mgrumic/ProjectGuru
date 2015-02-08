/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import projectguru.controllers.TeamOfficeController.UserWrapper;
import projectguru.controllers.util.SerbianLocalDateStringConverter;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.TaskHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.utils.FormLoader;
import projectguru.utils.ProjectGuruUtilities;

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
    private TextArea description;
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

    private boolean edit = false;

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
            LocalDate startLDate = start.getValue();
            LocalDate endLDate = ends.getValue();
            LocalDate dedlineLDate = dedline.getValue();
            String strMenHours = menHours.getText();

            if (strName == null
                    || strDescr == null
                    || strMenHours == null
                    || dedlineLDate == null) {
                FormLoader.showInformationDialog("Напомена", "Нисте попунили сва поља!");
                return;
            }
            if (!ProjectGuruUtilities.tryParseInt(strMenHours)) {
                FormLoader.showInformationDialog("Напомена", "Поље човјек/часова није коректно попуњено!");

                return;
            }
            Integer intMenHours = (new Integer(strMenHours));
            Date startDate;
            Date endDate = null;

            if (startLDate == null) {
                startDate = new Date();
            } else {
                startDate = Date.from(startLDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            }
            if (endLDate != null) {
                endDate = Date.from(endLDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            }

            Task tmpTask = null;
            if (edit) {
                tmpTask = user.getTaskHandler().getUpdatedTask(task);
            } else {
                tmpTask = new Task();
            }
            tmpTask.setName(strName);
            tmpTask.setStartDate(startDate);
            tmpTask.setEndDate(endDate);
            tmpTask.setDescription(strDescr);
            tmpTask.setDeadline(Date.from(dedlineLDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            tmpTask.setAssumedManHours(Integer.parseInt(strMenHours));

            TaskHandler taskJpa = user.getTaskHandler();
            try {

                boolean result;
                if (edit) {
                    result = taskJpa.editSubtask(tmpTask);
                } else {
                    /* ovo ne bi trebalo biti jer bi dodavanje projekta trebalo odmah dodati i root task */
                    if (parentTask == null) {
                        project.setIDRootTask(tmpTask);
                        user.getProjectHandler().setRootTask(project, tmpTask);
                        user.getTaskHandler().setChef(tmpTask, user.getUser());
                        result = true;
                    } else {
                        result = taskJpa.addSubtask(parentTask, tmpTask);
                    }
                }
                /* u slucaju da dodajemo, ne daj boze, root task, ovo ne smije proci */
                if (selectedMembers != null && result == true && task != null) {
                    Iterator<UserWrapper> itr = selectedMembers.iterator();
                    while (itr.hasNext()) {
                        User user = itr.next().getUser();
                        if(!tmpTask.getWorksOnTaskList()
                                .stream()
                                .anyMatch(
                                        (wot) -> wot
                                                .getWorksOnTaskPK()
                                                .getUsername()
                                                .equals(user.getUsername()))
                                ){
                            
                            
                            taskJpa.addMember(tmpTask, user);
                            
                            
                        }
                    }
                }
                controller.addNodeToTree(tmpTask);
                controller.loadTaskTree(project);
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
    @FXML
    private Button btnHelp;
    @FXML
    private Button btnBack;
    
    
    private Task parentTask;

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public void initialize(URL url, ResourceBundle rb
    ) {

        btnNext.setOnMouseClicked(eventOnClickNext);
        btnBack.setOnMouseClicked(eventOnClickBack);
        btnFinish.setOnMouseClicked(eventOnClickFinish);
        btnHelp.setOnMouseClicked(eventOnClickHelp);

        start.setValue(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
        start.setConverter(new SerbianLocalDateStringConverter());
        ends.setConverter(new SerbianLocalDateStringConverter());
        

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

    private void defineObservableList() {

        if(edit){
            selectedMembers = FXCollections.observableArrayList(
                    user.getTaskHandler().getAllMembers(task)
                    .stream()
                    .map((u)-> new UserWrapper(u))
                    .collect(Collectors.toList())
            );
            
            allMembers = FXCollections.observableArrayList(
                user.getTaskHandler().getAddableMembers(task)
                .stream()
                .map((u)-> new UserWrapper(u))
                .collect(Collectors.toList())
            );
            
        }else{
            selectedMembers = FXCollections.observableArrayList(new ArrayList());
            
            if(parentTask == null){
                /* root zadatak */
                allMembers = FXCollections.observableArrayList(
                        user.getProjectHandler().getAllMembers(project)
                        .stream()
                        .map((u)-> new UserWrapper(u))
                        .collect(Collectors.toList())
                );
            }else{
                /* novi zadatak, kao podzadatak parentTaska */
                allMembers = FXCollections.observableArrayList(
                        user.getTaskHandler().getAllMembers(parentTask)
                        .stream()
                        .map((u)-> new UserWrapper(u))
                        .collect(Collectors.toList())
                );
            }
        }

    }

    public void setController(TeamOfficeController controller) {
        this.controller = controller;

    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public void load(){

        if (edit) {
            name.setText(task.getName());
            description.setText(task.getDescription());
            start.setValue(Instant.ofEpochMilli(task.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            dedline.setValue(Instant.ofEpochMilli(task.getDeadline().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            if (task.getEndDate() != null) {
                ends.setValue(task.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            menHours.setText(Integer.toString(task.getAssumedManHours()));;
        }
        
        defineObservableList();
    }
}
