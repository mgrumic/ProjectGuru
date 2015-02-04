/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import projectguru.entities.Privileges;

import projectguru.entities.User;
import projectguru.handlers.LoggedUser;

/**
 * FXML Controller class
 *
 * @author ZM
 */
public class FormUserAccountsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private PasswordField pfConfirmPassword;

    @FXML
    private ComboBox<ApplicationPrivileges> cbPrivileges;

    @FXML
    private Button btnAddUser;

    @FXML
    private TextField tfLastName;

    @FXML
    private Button btnEdit;

    @FXML
    private TextField tfUsername;

    @FXML
    private TextField tfFirstName;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private TextField tfSearch;

    @FXML
    private ListView<UserWrapper> listViewUsers;

    @FXML
    private CheckBox checkAccountActive;

    @FXML
    private Button btnExit;

    /**
     *
     * Moje varijable
     */
    LoggedUser user;
    ObservableList<UserWrapper> users;

    @FXML
    void btnAddUserPressed(ActionEvent event) {

    }

    @FXML
    void btnEditUserPressed(ActionEvent event) {

    }

    @FXML
    void btnExitPressed(ActionEvent event) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        listViewUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UserWrapper>() {
            @Override
            public void changed(ObservableValue<? extends UserWrapper> observable, UserWrapper oldValue, UserWrapper newValue) {
                if (newValue != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            changeFields(newValue);
                        }
                    });
                }
            }
        });
        tfSearch.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                    Object newVal) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        searchUsers((String) oldVal, (String) newVal);
                    }
                });
            }
        });

        cbPrivileges.setCellFactory(new Callback<ListView<ApplicationPrivileges>, ListCell<ApplicationPrivileges>>() {
            @Override
            public ListCell<ApplicationPrivileges> call(ListView<ApplicationPrivileges> p) {
                return new ListCell<ApplicationPrivileges>() {
                    @Override
                    protected void updateItem(ApplicationPrivileges item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            }
        });
        ApplicationPrivileges admin = new ApplicationPrivileges("Администратор", Privileges.ADMIN.ordinal());
        ApplicationPrivileges chefProject = new ApplicationPrivileges("Шеф пројекта", Privileges.CHEF.ordinal());
        ApplicationPrivileges member = new ApplicationPrivileges("Члан", Privileges.MEMBER.ordinal());
        ApplicationPrivileges insight = new ApplicationPrivileges("Надзор", Privileges.INSIGHT.ordinal());
        
        cbPrivileges.getItems().addAll(insight, member, chefProject, admin);
        
    }

    public void setUser(LoggedUser user) {
        this.user = user;
        loadUsers();
    }

    private void loadUsers() {
        users = FXCollections.observableArrayList(
                user.getUserHandler().getAllUsers()
                .stream()
                .map((member) -> new UserWrapper(member))
                .collect(Collectors.toList()));
        listViewUsers.setItems(users);
    }

    private void changeFields(UserWrapper userWrapper) {

        User user = userWrapper.getUser();

        tfUsername.setText(user.getUsername());
        pfPassword.setText(user.getPassword());
        pfConfirmPassword.setText(user.getPassword());
        checkAccountActive.setSelected(user.getActivated());
        tfFirstName.setText(user.getFirstName());
        tfLastName.setText(user.getLastName());

    }

    private void searchUsers(String oldValue, String newValue) {
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            listViewUsers.setItems(users);
        }
        if (newValue.length() == 0) {
            loadUsers();
            return;
        }
        String value = newValue.toUpperCase();
        ObservableList<UserWrapper> subentries = FXCollections.observableArrayList();
        for (Object entry : listViewUsers.getItems()) {
            UserWrapper entryUser = (UserWrapper) entry;
            if (entryUser.getUser().getUsername().toUpperCase().startsWith(value)) {
                subentries.add(entryUser);
            }
        }
        listViewUsers.setItems(subentries);
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
    }

    private static class ApplicationPrivileges {

        private String name;
        private int value;

        public ApplicationPrivileges(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
