package projectguru.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.handlers.exceptions.UserNotTaskMemberException;

public class FormSetActiveTaskController {

    
    @FXML
    private ListView<UserWrapper> listMembers;

    @FXML
    private Button btnActivateForAll;

    @FXML
    private Button btnActivateForMember;

    @FXML
    void btnActivateForMemberPressed(ActionEvent event) {
        UserWrapper item = listMembers.getSelectionModel().getSelectedItem();
        if(item != null){
            try {
                System.out.println("Rezultat :" + user.getTaskHandler().setActiveTask(task, item.getUser()));
            } catch (EntityDoesNotExistException ex) {
                Logger.getLogger(FormSetActiveTaskController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (StoringException ex) {
                Logger.getLogger(FormSetActiveTaskController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InsuficientPrivilegesException ex) {
                Logger.getLogger(FormSetActiveTaskController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UserNotTaskMemberException ex) {
                Logger.getLogger(FormSetActiveTaskController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    void btnActivateForAllPressed(ActionEvent event) {
        
        try {
            System.out.println("Rezultat: " + user.getTaskHandler().startTask(task));
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(FormSetActiveTaskController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (StoringException ex) {
            Logger.getLogger(FormSetActiveTaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Moje varijable
     */
    private Task task;
    private LoggedUser user;
    private ObservableList<UserWrapper> members;
    
    @FXML
    void initialize() {
        
        assert listMembers != null : "fx:id=\"listMembers\" was not injected: check your FXML file 'FormSetActiveTask.fxml'.";
        assert btnActivateForAll != null : "fx:id=\"btnActivateForAll\" was not injected: check your FXML file 'FormSetActiveTask.fxml'.";
        assert btnActivateForMember != null : "fx:id=\"btnActivateForMember\" was not injected: check your FXML file 'FormSetActiveTask.fxml'.";

    }
    
    public void setUserAndTask(LoggedUser user, Task task){
        this.task = task;
        this.user = user;
        loadMembers();
    }
    private void loadMembers(){
        members = FXCollections.observableArrayList(
                user.getTaskHandler().getAllMembers(task)
                .stream()
                .map((member) -> new UserWrapper(member))
                .collect(Collectors.toList()));
        listMembers.setItems(members);
    }
    
    private static class UserWrapper {

        private User user;

        public UserWrapper(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return user.getUsername();
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof UserWrapper)) {
                return false;
            }
            UserWrapper other = (UserWrapper) object;
            return user.equals(other.getUser());
        }
    }

}
