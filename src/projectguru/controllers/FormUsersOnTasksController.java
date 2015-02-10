/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import com.sun.javafx.sg.prism.NGCanvas;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableArrayBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import netscape.security.Privilege;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import projectguru.entities.Privileges;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.entities.WorksOnTask;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.handlers.exceptions.UserNotTaskMemberException;
import projectguru.utils.FormLoader;

/**
 * FXML Controller class
 *
 * @author marko
 */
public class FormUsersOnTasksController implements Initializable {

    @FXML
    private TableView<UserOnTaskWrapper> tblUsers;

     @FXML
    private TableColumn<UserOnTaskWrapper, String> columnUsername;

    @FXML
    private Button btnSave;

    @FXML
    private TableColumn<UserOnTaskWrapper, String> columnNameLastName;

    @FXML
    private CheckBox chbChef;

    @FXML
    private CheckBox chbActive;

    @FXML
    private TableColumn<UserOnTaskWrapper, String> columnActive;

    @FXML
    private Button btnReject;
    
    @FXML
    private Button btnAddMember;
    
    @FXML
    private Button btnViewTimetable;
    
    @FXML
    private TableColumn<UserOnTaskWrapper, String> columnChef;
    
    @FXML
    private Button btnDeleteMember;

    @FXML
    void btnSave_OnActive(ActionEvent event) {
        save();
    }

    @FXML
    void btnReject_OnAction(ActionEvent event) {
        reject();
    }
    
        @FXML
    void btnAddMember_OnAction(ActionEvent event) {
        add();
    }

    @FXML
    void btnDeleteMember_OnAction(ActionEvent event) {
        delete();
    }

    @FXML
    void btnViewTimetable_OnAction(ActionEvent event) {
            FormLoader.showErrorDialog("Још није имплементирано", "Тражена функционалност још није имплементирана");
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        btnReject.setVisible(false);
        btnSave.setVisible(false);
        columnUsername.setCellValueFactory((r) -> new SimpleObjectProperty<>(r.getValue().getUsername()));
        columnNameLastName.setCellValueFactory((r) -> new SimpleObjectProperty<>(r.getValue().getNameLastName()));
        columnActive.setCellValueFactory((r) -> new SimpleObjectProperty<>(r.getValue().isWorking() ? "Да" : "Ne"));
        columnChef.setCellValueFactory((r)-> new SimpleObjectProperty<>(r.getValue().isChef() ? "Шеф" : ""));
        
        
    }    
    
    public void load(){
        
        User chef = loggedUser.getTaskHandler().getChef(task);
        
        ischef = chef != null && chef.getUsername().equals(loggedUser.getUser().getUsername());
        
        btnAddMember.setDisable(!ischef);
        btnDeleteMember.setDisable(!ischef);
        btnReject.setDisable(!ischef);
        btnSave.setDisable(!ischef);
        chbActive.setDisable(!ischef);
        chbChef.setDisable(!ischef);
        
        loadUsers();
  
    }
    
    private void loadUsers(){
        
        List<User> users = loggedUser.getTaskHandler().getAllMembers(task);
        
        usersOnTask = FXCollections.observableArrayList(
                users.stream().map((u) -> new UserOnTaskWrapper(u, task)).collect(Collectors.toList())
        );
        
        tblUsers.setItems(usersOnTask);
        
        tblUsers.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<UserOnTaskWrapper>(){

                    @Override
                    public void changed(ObservableValue<? extends UserOnTaskWrapper> observable, UserOnTaskWrapper oldValue, UserOnTaskWrapper newValue) {
                        tblUsersSelectedItemChanged(oldValue, newValue);
                    }
                    
                }
        );
        
        
    }
    
    private void save(){
        UserOnTaskWrapper item = tblUsers.getSelectionModel().getSelectedItem();
        WorksOnTask wot = item.getWorksOnTask();
        
        if(chbActive.isSelected() != wot.getWorking() ){

            try {
                
                if (chbActive.isSelected()) {

                    loggedUser.getTaskHandler().setActiveTask(task, item.getUser());

                }else{
                    
                    loggedUser.getTaskHandler().deactivateUserFromTask(task, item.getUser());
                    
                }

            } catch (EntityDoesNotExistException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showErrorDialog("Грешка", "Чини се да, или задатак или овај корисник више не постоје у бази.");
            } catch (StoringException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом уписа у базу.");
            } catch (InsuficientPrivilegesException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showInformationDialog("Недовољне привилегије", "Грешка се десила највјероватније усљед тога што немате довољне привилегије над задатком на којем одабрани корисник тренутно ради.");
            } catch (UserNotTaskMemberException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showErrorDialog("Грешка", "Чини се да овај корисник више није члан задатка.");
            }
            
        }
        
        if(chbChef.isSelected() != item.isChef()){
            try {
                
                if (chbChef.isSelected()) {

                    loggedUser.getTaskHandler().setChef(task, item.getUser());

                }else{
                    
                    loggedUser.getTaskHandler().removeChef(task);
                    
                }

            } catch (EntityDoesNotExistException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showErrorDialog("Грешка", "Чини се да, или задатак више не постоје у бази.");
            } catch (StoringException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом уписа у базу.");
            }
        }
        
        load();
        
    }
    
    private void add(){
        
        try {
            User newUser = FormLoader.loadFormAddableMembers(loggedUser, task);
            
            loggedUser.getTaskHandler().addMember(task, newUser);
            
            
        } catch (IOException ex) {
            Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Чини се да, или задатак или овај корисник више не постоје у бази.");
        } catch (StoringException ex) {
            Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом уписа у базу.");
        } catch (InsuficientPrivilegesException ex) {
            Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
            FormLoader.showInformationDialog("Недовољне привилегије", "Грешка се десила највјероватније усљед тога што више немате привилегије да додате учесника на задатак.");
        }
        
        loadUsers();
    }
    
    private void delete(){
        
        boolean conf = FormLoader.showConfirmationDialog("Да ли сте сигурни?", "Да ли сте сигурни да желите уконити корисника са задатка?");
        
        if(!conf){
            return;
        }

        UserOnTaskWrapper item = tblUsers.getSelectionModel().getSelectedItem();
        
            try {
                loggedUser.getTaskHandler().removeMember(task, item.getUser());

            } catch (EntityDoesNotExistException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showErrorDialog("Грешка", "Чини се да, или задатак или овај корисник више не постоје у бази.");
            } catch (StoringException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showErrorDialog("Грешка", "Десила се грешка приликом уписа у базу.");
            } catch (InsuficientPrivilegesException ex) {
                Logger.getLogger(FormUsersOnTasksController.class.getName()).log(Level.SEVERE, null, ex);
                FormLoader.showInformationDialog("Недовољне привилегије", "Грешка се десила највјероватније усљед тога што немате довољне привилегије над задатком на којем одабрани корисник тренутно ради.");
            }
        
            load();
    }
    
    private void reject(){
        UserOnTaskWrapper item = tblUsers.getSelectionModel().getSelectedItem();
        if(item != null){
            chbActive.setSelected(item.isWorking());
            chbChef.setSelected(item.isChef());
        }
    }
    
    private void tblUsersSelectedItemChanged(UserOnTaskWrapper oldValue, UserOnTaskWrapper newValue){
        
        if(newValue != null){
            if(ischef){
                btnReject.setDisable(false);
                btnSave.setDisable(false);
                chbActive.setDisable(false);
                chbChef.setDisable(false);
                
            }
            
            chbActive.setSelected(newValue.isWorking());
            chbChef.setSelected(newValue.isChef());
            
        }else{
            btnReject.setDisable(true);
            btnSave.setDisable(true);
            chbActive.setSelected(false);
            chbChef.setSelected(false);
            chbActive.setDisable(true);
        }
        
    }
    
    private ObservableList<UserOnTaskWrapper> usersOnTask;
    
    private boolean ischef;
    
    private LoggedUser loggedUser;
    
    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LoggedUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }
    
    
    private static class UserOnTaskWrapper {
        private User user;
        private WorksOnTask worksOnTask;
        private String username;
        private String nameLastName;
        private boolean working;
        private boolean chef;

        public boolean isChef() {
            return chef;
        }

        public void setChef(boolean chef) {
            this.chef = chef;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public WorksOnTask getWorksOnTask() {
            return worksOnTask;
        }

        public void setWorksOnTask(WorksOnTask worksOnTask) {
            this.worksOnTask = worksOnTask;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNameLastName() {
            return nameLastName;
        }

        public void setNameLastName(String nameLastName) {
            this.nameLastName = nameLastName;
        }

        public boolean isWorking() {
            return working;
        }

        public void setWorking(boolean working) {
            this.working = working;
        }
        
        
        
        public UserOnTaskWrapper(User user, Task task){
            this.user = user;
            this.username = user.getUsername();
            this.nameLastName = user.getFirstName() + " " + user.getLastName();
            
            worksOnTask = user.getWorksOnTaskListNonRemoved()
                    .stream().filter((wot)-> wot.getWorksOnTaskPK().getIDTask() == task.getId())
                    .findFirst()
                    .orElse(null);
            
            if(worksOnTask != null){
                working = worksOnTask.getWorking();
                chef = worksOnTask.getPrivileges() == Privileges.CHEF.ordinal();
            }else{
                working = false;
                chef = false;
            }
            
        }
    }
    
}
