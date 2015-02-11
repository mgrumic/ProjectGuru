/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;

/**
 * FXML Controller class
 *
 * @author marko
 */
public class FormAddableMembersController implements Initializable {

    
    @FXML
    private Button btnCancel;

    @FXML
    private ListView<User> lstAddableUsers;

    @FXML
    private Button btnConfirm;

    @FXML
    void btnConfirm_OnAction(ActionEvent event) {
        user = lstAddableUsers.getSelectionModel().getSelectedItem();
        
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void btnCancel_OnAction(ActionEvent event) {
        Stage stage = (Stage)btnCancel.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lstAddableUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {

            @Override
            public ListCell<User> call(ListView<User> param) {
               return new ListCell<User>(){
                   @Override
                   protected void updateItem(User item, boolean empty){
                       if(item == null || empty){
                           setText("");
                       }else{
                           setText(item.getFirstName() + " " + item.getLastName() + " (" + item.getUsername() + ")");
                       }
                   }
               };
            }
        });
        
    }
    
    private ObservableList<User> addableUsers;
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
    
    
    
    public void load(){
        
        loadUsers();
        
    }
    
    private void loadUsers(){
        addableUsers = FXCollections.observableArrayList(
                loggedUser.getTaskHandler().getAddableMembers(task)
        );
        
        lstAddableUsers.setItems(addableUsers);
        
    }
    
}
