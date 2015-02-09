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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static java.time.temporal.TemporalQueries.localDate;
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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import projectguru.controllers.TeamOfficeController.UserWrapper;
import projectguru.controllers.util.SerbianLocalDateStringConverter;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.jpa.handlers.JpaProjectHandler;
import projectguru.utils.FormLoader;
import projectguru.utils.ProjectGuruUtilities;

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
    private TextArea description;
    @FXML
    private DatePicker start;
    @FXML
    private DatePicker ends;
    @FXML
    private Button btnHelp;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblFormName;
    @FXML
    private TextField txtManHours;
    @FXML
    private GridPane paneRootTaskAdditions;
    @FXML
    private DatePicker dpDeadline;
    
    
    private LoggedUser user;

    private ObservableList<UserWrapper> allMembers = null;
    private ObservableList<UserWrapper> selectedMembers = null;
    private Project project;

    private boolean edit = false;

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
                    + "креирао пројекат морате унијети сва поља. "
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
            String strBudget = budget.getText();

            if (strName == null
                    || strDescr == null
                    || strBudget == null) {
                FormLoader.showInformationDialog("Напомена", "Нисте попунили сва поља!");

                return;
            }
            if (!ProjectGuruUtilities.tryParseDouble(strBudget)) {
                FormLoader.showInformationDialog("Напомена", "Поље буџет није коректно попуњено!");
                return;
            }
            
            int manhours = 0;
            Date rootDeadline = null;
            
            if(!edit){
            
                try{
                    manhours = Integer.parseInt(txtManHours.getText().trim());
                }catch(Exception ex){
                    FormLoader.showErrorDialog("Грешка", "Упишите одговарајућу вриједност у поље човјек-часови.");
                    return;
                }
                
                try{
                    if(dpDeadline.getValue().isBefore(LocalDate.now(ZoneId.systemDefault()))){
                        FormLoader.showErrorDialog("Грешка", "Вриједност крајњег рока је невалидна.");
                        return;
                    }
                    rootDeadline = Date.from(dpDeadline.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                }catch(Exception ex){
                    FormLoader.showErrorDialog("Грешка", "Вриједност крајњег рока је невалидна.");
                    return;
                }
          
            }

            Double dou = (new Double(strBudget));
            Integer id = null;
            Project newProject = null;
            /*
             *   Ukoliko se radi edit, moras dovuci projekat iz baze
             */
            if (edit) {
                try {
                    newProject = user.getProjectHandler().getUpdatedProject(project);
                } catch (EntityDoesNotExistException ex) {
                    FormLoader.showInformationDialog("Грешка", "Пројекат не посотји");
                }
                
            } else {
                newProject = new Project();
            }

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
            
            newProject.setBudget(BigDecimal.valueOf(dou.longValue()));
            newProject.setDescription(strDescr);
            newProject.setName(strName);
            newProject.setStartDate(startDate);
            newProject.setEndDate(endDate);


            try {
                ProjectHandler projectJpa = user.getProjectHandler();
                if (edit) {
                    projectJpa.editProject(newProject);
                } else {
                    projectJpa.createProject(newProject);
                    project = newProject;
                }
                if (selectedMembers != null) {
                    Iterator<UserWrapper> itr = selectedMembers.iterator();
                    while (itr.hasNext()) {

                        projectJpa.addMember(
                                newProject,
                                itr.next().getUser()
                        );
                    }
                }
                
                if(!edit){
                    
                    Task root = new Task();
                    
                    root.setDescription(strDescr);
                    root.setName(strName);
                    root.setAssumedManHours(manhours);
                    root.setDeadline(rootDeadline);
                    
                    user.getProjectHandler().setRootTask(newProject, root);
                    user.getTaskHandler().setChef(root, user.getUser());
                }
                Stage stage = (Stage) btnFinish.getScene().getWindow();
                stage.close();
            } catch (InsuficientPrivilegesException ex) {
                ex.printStackTrace();
                FormLoader.showInformationDialog("Напомена", "Немате довољно привилегија !");
            } catch (StoringException ex) {
                ex.printStackTrace();
                FormLoader.showInformationDialog("Напомена", "Грешка са базом !");
            } catch (EntityDoesNotExistException ex) {
                ex.printStackTrace();
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
        btnBack.setOnMouseClicked(eventOnClickBack);
        btnFinish.setOnMouseClicked(eventOnClickFinish);
        btnHelp.setOnMouseClicked(eventOnClickHelp);
        start.setValue(Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
        dpDeadline.setConverter(new SerbianLocalDateStringConverter());
        ends.setConverter(new SerbianLocalDateStringConverter());
        start.setConverter(new SerbianLocalDateStringConverter());
    }

    public void setUser(LoggedUser user) {
        this.user = user;
        if (allMembers == null) {
            allMembers = FXCollections.observableArrayList(
                    user.getUserHandler().getAllUsers()
                    .stream()
                    .map((member) -> new UserWrapper(member))
                    .collect(Collectors.toList())
            );
        }
        if (selectedMembers == null) {
            selectedMembers = FXCollections.observableArrayList();
        }
    }

    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            
            edit = true;
            
            paneRootTaskAdditions.setVisible(false);
            
            lblFormName.setText("Промјена ставки пројекта");
            name.setText(project.getName());
            
            if (project.getDescription() != null) {
                description.setText(project.getDescription());
            }
            
            start.setValue(Instant.ofEpochMilli(project.getStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            if (project.getEndDate() != null) {
                ends.setValue(Instant.ofEpochMilli(project.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
            }

            budget.setText(project.getBudget().toString());
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
        }else {
            lblFormName.setText("Нови пројекат ");
        }

    }
    
    public Project getProject(){
        return project;
    }

}
