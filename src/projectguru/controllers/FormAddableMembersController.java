/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;
import javafx.util.Callback;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.tasktree.TaskNode;

/**
 * FXML Controller class
 *
 * @author marko
 */
public class FormAddableMembersController implements Initializable {

    @FXML
    private Button btnCancel;

    @FXML
    private ListView<UserWrapper> lstAddableUsers;

    @FXML
    private Button btnConfirm;

    @FXML
    void btnConfirm_OnAction(ActionEvent event) {
        UserWrapper item = lstAddableUsers.getSelectionModel().getSelectedItem();
        if (item != null) {
            user = item.getUser();
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        }

    }

    @FXML
    void btnCancel_OnAction(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private ObservableList<UserWrapper> addableUsers;
    private LoggedUser loggedUser;
    private Task task;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoggedUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoggedUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void load() {

        loadUsers();

    }

    private void loadUsers() {
        addableUsers = FXCollections.observableArrayList(
                loggedUser.getTaskHandler().getAddableMembers(task)
                .stream()
                .map((member) -> new UserWrapper(member))
                .collect(Collectors.toList())
        );

        lstAddableUsers.setItems(addableUsers);
        lstAddableUsers.getSelectionModel().select(null);

    }

    public static class UserWrapper {

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
            return user.getFirstName() + " " + user.getLastName() + " (" + user.getUsername() + ")";
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
