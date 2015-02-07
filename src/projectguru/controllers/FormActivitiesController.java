/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.FormatStringConverter;
import netscape.security.Privilege;
import projectguru.controllers.util.SerbianLocalDateStringConverter;
import projectguru.entities.Activity;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.utils.FormLoader;

/**
 * FXML Controller class
 *
 * @author marko
 */
public class FormActivitiesController implements Initializable {

    @FXML
    private TableColumn<ActivityWrapper, String> columnCreatorName;

    @FXML
    private TableColumn<ActivityWrapper, String> columnRemark;

    @FXML
    private TableColumn<ActivityWrapper, String> columnTaskName;

    @FXML
    private TableColumn<ActivityWrapper, String> columnDate;

    @FXML
    private TableColumn<ActivityWrapper, String> columnName;

    @FXML
    private TextField txtRemark;

    @FXML
    private TableView<ActivityWrapper> tblActivities;

    @FXML
    private TextField txtName;

    @FXML
    private TextArea txtDescription;

    @FXML
    private Button btnNewActivity;

    @FXML
    private CheckBox chbOnlyMyActivity;

    @FXML
    private Button btnReject;

    @FXML
    private Button btnDeleteActivity;

    @FXML
    private Button btnSaveChanges;

    @FXML
    private TextField txtTaskName;

    @FXML
    private DatePicker dpDate;

    @FXML
    private TextField txtCreator;

    @FXML
    private CheckBox chbOnlyThisTask;

    @FXML
    void btnSaveChanges_OnAction(ActionEvent event) {
           saveChanges();
    }

    @FXML
    void btnReject_OnAction(ActionEvent event) {
        ActivityWrapper aw = tblActivities.getSelectionModel().getSelectedItem();
        setFormData(aw);
    }

    @FXML
    void btnNewActivity_OnAction(ActionEvent event) {
        changeMode(true);
    }

    @FXML
    void btnDeleteActivity_OnAction(ActionEvent event) {
        deleteActivity();
    }
    
    @FXML
    void chbOnlyMyActivity_OnAction(ActionEvent event) {
        loadActivities();
    }

    @FXML
    void chbOnlyThisTask_OnAction(ActionEvent event) {
        loadActivities();
    }

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnCreatorName.setCellValueFactory( (rowData) -> rowData.getValue().getCreatorName());
        columnDate.setCellValueFactory((rowData) -> new SimpleObjectProperty(rowData.getValue().getDate()));
        columnName.setCellValueFactory((rowData) -> rowData.getValue().getName());
        columnRemark.setCellValueFactory((rowData) -> rowData.getValue().getRemark());
        columnTaskName.setCellValueFactory((rowData) -> rowData.getValue().getTaskName());
        
        dpDate.setConverter(new SerbianLocalDateStringConverter());
        
        

    }    
    
    public void load(){
        ischef = loggedUser.getTaskHandler().checkTaskChefPrivileges(task);
        if(ischef)
        {
            ismember = true;
        }else{
            ismember = loggedUser.getTaskHandler().checkMemberPrivileges(task);
        }
        
        
        if(!ismember){ 
            btnNewActivity.setDisable(true);
        }

        loadActivities();
        
    }
    
    
    public void loadActivities(){
        try {
            
            
            boolean onlyForCurrentUser = chbOnlyMyActivity.isSelected();
            boolean includeSubtask = !chbOnlyThisTask.isSelected();
            
            List<Activity> activities = loggedUser.getActivityHandler().findActivitiesForTask(task, includeSubtask, onlyForCurrentUser);
        
            activityWrappers = FXCollections.observableArrayList(
                    activities.stream().map((a) -> new ActivityWrapper(a)).collect(Collectors.toList())
            );
        
            tblActivities.setItems(activityWrappers);
            
            tblActivities.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<ActivityWrapper> () {
                        @Override
                        public void changed(ObservableValue<? extends ActivityWrapper> observable, ActivityWrapper oldValue, ActivityWrapper newValue) {
                            tblActivitiesSelectedItemChanged(observable, oldValue, newValue);
                        }
                    }
            );
            
        
        } catch (InsuficientPrivilegesException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Чини се да овај задатак више не постоји у бази.");
        } catch (StoringException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом приступа бази. За више информација погледајте лог датотеку.");
        
        }
        
        
        
        
    }
    
    private void tblActivitiesSelectedItemChanged(ObservableValue<? extends ActivityWrapper> observable, ActivityWrapper oldValue, ActivityWrapper newValue){
        setFormData(newValue);
        if(newValue != null && loggedUser.getActivityHandler().checkActivityChefPrivileges(newValue.getActivity())){
            btnDeleteActivity.setDisable(false);
        }else{
            btnDeleteActivity.setDisable(true);
        }
    }
    
    private void setFormData(ActivityWrapper newValue){
        
        if(newValue == null){
            return;
        }
        
        txtCreator.setText(newValue.getCreatorName().getValue());
        txtDescription.setText(newValue.getDescription().getValue());
        txtName.setText(newValue.getName().getValue());
        txtRemark.setText(newValue.getRemark().getValue());
        txtTaskName.setText(newValue.getTaskName().getValue());
        dpDate.setValue(newValue.getDate());
        
        if(newValue.getActivity().getWorksOnTask().getWorksOnTaskPK()
                .getUsername().compareTo(
                        loggedUser.getUser().getUsername()) == 0){
            
            btnReject.setDisable(false);
            btnSaveChanges.setDisable(false);
            
        }else{
            
            btnReject.setDisable(true);
            btnSaveChanges.setDisable(true);
        }
        
        changeMode(false);
    }
    
    private void deleteActivity(){
         Activity act = tblActivities.getSelectionModel().getSelectedItem().getActivity();
         
        try {
            loggedUser.getActivityHandler().deleteActivity(act);
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Овај задатак више не постоји у бази.");
        } catch (StoringException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом приступа бази. За више информација погледајте лог датотеку.");
        } catch (InsuficientPrivilegesException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Чини се да немате привилегије потребне за ову акцију.");
        }
        
        loadActivities();
    }
    
    private void saveChanges(){
        if(newMode){
            addActivity();
        }else{
            saveEdit();
        }
        changeMode(true);
        loadActivities();
    }

    
    private ObservableList<ActivityWrapper> activityWrappers;
    
    boolean ismember;
    boolean ischef;
    
    private LoggedUser loggedUser;

    public LoggedUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }
    
    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    
    private boolean newMode;
    
    private void changeMode(boolean newMode){
        if(newMode){
            txtCreator.setText(loggedUser.getUser().getUsername());
            txtDescription.setText("");
            txtName.setText("");
            txtRemark.setText("");
            txtTaskName.setText(task.getName());
            dpDate.setValue(LocalDate.now(ZoneId.systemDefault()));
            dpDate.setDisable(false);
        }else{
            dpDate.setEditable(false);
        }
        
        this.newMode = newMode;
    }
    
    private void addActivity(){

        Activity act = new Activity();
        
        Date date = Date.from(dpDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        act.setCreationDate(date);
        
        act.setDescription(txtDescription.getText());
        act.setName(txtName.getText());
        act.setRemark(txtRemark.getText());

                
        try {
            loggedUser.getTaskHandler().addActivity(task, act);
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Овај задатак више не постоји у бази.");
        } catch (StoringException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом приступа бази. За више информација погледајте лог датотеку.");
        } catch (InsuficientPrivilegesException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Чини се да немате привилегије потребне за ову акцију.");
        }
        
    }
    
    private void saveEdit(){
        Activity act = tblActivities.getSelectionModel().getSelectedItem().getActivity();
        
        Date date = Date.from(dpDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        if(!date.equals(act.getCreationDate())){
            act.setCreationDate(date);
        }
        
        if(txtName.getText().compareTo(act.getName()) != 0){
            act.setName(txtName.getText());
        }
        
        if(txtRemark.getText().compareTo(act.getRemark()) != 0){
            act.setRemark(txtRemark.getText());
        }
        
        if(txtDescription.getText().compareTo(act.getDescription()) != 0){
            act.setDescription(txtDescription.getText());
        }
        
        try {
            
            loggedUser.getActivityHandler().editActivity(act);
            
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Ова активност више не постоји у бази.");
        } catch (InsuficientPrivilegesException ex) {
             Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Чини се да немате привилегије потребне за ову акцију.");
        } catch (StoringException ex) {
            Logger.getLogger(FormActivitiesController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом приступа бази. За више информација погледајте лог датотеку.");
        }
        
    }
    
}

class ActivityWrapper {
        
        private StringProperty name;
        private StringProperty taskName;
        private StringProperty creatorName;
        private LocalDate date;
        private StringProperty remark;
        private IntegerProperty id;
        private StringProperty description;
        private Activity activity;

    public StringProperty getName() {
        return name;
    }

    public void setName(StringProperty name) {
        this.name = name;
    }

    public StringProperty getTaskName() {
        return taskName;
    }

    public void setTaskName(StringProperty taskName) {
        this.taskName = taskName;
    }

    public StringProperty getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(StringProperty creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public StringProperty getRemark() {
        return remark;
    }

    public void setRemark(StringProperty remark) {
        this.remark = remark;
    }

    public IntegerProperty getId() {
        return id;
    }

    public void setId(IntegerProperty id) {
        this.id = id;
    }

    public StringProperty getDescription() {
        return description;
    }

    public void setDescription(StringProperty description) {
        this.description = description;
    }
        
        

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

             
        
        
        public ActivityWrapper(Activity activity){
            User user = activity.getWorksOnTask().getUser();
            creatorName = new SimpleStringProperty(user.getFirstName() + " " + user.getLastName());
            taskName = new SimpleStringProperty(activity.getWorksOnTask().getTask().getName());
            date = ZonedDateTime.ofInstant(activity.getCreationDate().toInstant(), ZoneId.systemDefault()).toLocalDate();
            id = new SimpleIntegerProperty(activity.getId());
            name = new SimpleStringProperty(activity.getName());
            remark = new SimpleStringProperty(activity.getRemark());
            description = new SimpleStringProperty(activity.getDescription());
            this.activity = activity;
        }
        
    }

