/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import projectguru.entities.Privileges;

import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.utils.FormLoader;

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

    @FXML
    private Button btnCleanFields;

    /**
     *
     * Moje varijable
     */
    private LoggedUser user;
    private ObservableList<UserWrapper> users;
    private ObservableList<ApplicationPrivileges> privileges;

    @FXML
    void btnAddUserPressed(ActionEvent event) {
        if (checkFields() == true) {
            addUser();
        }
    }

    @FXML
    void btnEditUserPressed(ActionEvent event) {
        if (checkFields() == true) {
            editUser();
        }
    }

    @FXML
    void btnExitPressed(ActionEvent event) {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    void btnCleanFieldsPressed(ActionEvent event) {
        tfUsername.setText("");
        pfPassword.setText("");
        pfConfirmPassword.setText("");
        cbPrivileges.setValue(getItemWithPrivileges(0));
        checkAccountActive.setSelected(true);
        tfFirstName.setText("");
        tfLastName.setText("");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnAddUser.setGraphic(new ImageView(new Image(FormUserAccountsController.class
                .getResourceAsStream("/projectguru/images/add_user.png"))));

        btnEdit.setGraphic(new ImageView(new Image(FormUserAccountsController.class
                .getResourceAsStream("/projectguru/images/edit_user.png"))));

        btnCleanFields.setGraphic(new ImageView(new Image(FormUserAccountsController.class
                .getResourceAsStream("/projectguru/images/clean.png"))));

        btnExit.setGraphic(new ImageView(new Image(FormUserAccountsController.class
                .getResourceAsStream("/projectguru/images/close.png"))));
        
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

        privileges = FXCollections.observableArrayList();
        privileges.add(new ApplicationPrivileges("Обичан корисник", Privileges.NO_PRIVILEGES.ordinal()));
        //privileges.add(new ApplicationPrivileges("Екстерни корисник", Privileges.EXTERN.ordinal()));
        // privileges.add(new ApplicationPrivileges("Надзор", Privileges.INSIGHT.ordinal()));
        //privileges.add(new ApplicationPrivileges("Члан", Privileges.MEMBER.ordinal()));
        privileges.add(new ApplicationPrivileges("Шеф пројекта", Privileges.CHEF.ordinal()));
        privileges.add(new ApplicationPrivileges("Администратор", Privileges.ADMIN.ordinal()));

        cbPrivileges.setItems(privileges);
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

        cbPrivileges.setValue(getItemWithPrivileges(user.getAppPrivileges()));

    }

    private ApplicationPrivileges getItemWithPrivileges(int privilege) {
        for (ApplicationPrivileges pr : privileges) {
            if (pr.getValue() == privilege) {
                return pr;
            }
        }
        return null;
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

    private boolean checkFields() {
        boolean result = true;
        if (tfUsername.getText().equals("")) {
            FormLoader.showInformationDialog("Грешка", "Нисте унијели корисничко име !");
            result = false;
        } else if (pfPassword.getText().equals("")) {
            FormLoader.showInformationDialog("Грешка", "Нисте унијели шифру !");
            result = false;
        } else if (pfConfirmPassword.getText().equals("")) {
            FormLoader.showInformationDialog("Грешка", "Нисте потврдили шифру !");
            result = false;
        } else if (!pfPassword.getText().equals(pfConfirmPassword.getText())) {
            FormLoader.showInformationDialog("Грешка", "Нисте потврдили шифру !");
            result = false;
        } else if (tfFirstName.getText().equals("")) {
            FormLoader.showInformationDialog("Грешка", "Нисте унијели име корисника !");
            result = false;
        } else if (tfLastName.getText().equals("")) {
            FormLoader.showInformationDialog("Грешка", "Нисте унијели презиме корисника !");
            result = false;
        }
        return result;
    }

    private boolean addUser() {
        String username = tfUsername.getText();
        for (UserWrapper item : listViewUsers.getItems()) {
            if (item.getUser().getUsername().equals(username)) {
                FormLoader.showInformationDialog("Грешка", "Постоји корисник са корисничким именом " + username + "!");
                return false;
            }
        }
        boolean result = false;
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(pfPassword.getText());
        newUser.setAppPrivileges(cbPrivileges.getValue().getValue());
        newUser.setActivated(checkAccountActive.isSelected());
        newUser.setFirstName(tfFirstName.getText());
        newUser.setLastName(tfLastName.getText());

        try {
            result = user.getUserHandler().addUser(newUser);
            if (result == true) {
                FormLoader.showInformationDialog("Обавјештење", "Корисник је успјешно додан.");
                loadUsers();
            }
            return result;
        } catch (StoringException ex) {
            FormLoader.showInformationDialog("Грешка са базом", "Није успјело додавање новог корисника !");
        }
        return false;
    }

    private boolean editUser() {
        UserWrapper userItem = listViewUsers.getSelectionModel().getSelectedItem();
        if (userItem == null) {
            FormLoader.showInformationDialog("Грешка", "Морате изабрати корисника");
            return false;
        }
        User selected = userItem.getUser();
        String username = tfUsername.getText();
        if (!selected.getUsername().equals(username)) {
            FormLoader.showInformationDialog("Грешка", "Није дозвољена промјена корисничког имена");
            tfUsername.setText(selected.getUsername());
            return false;
        }
        boolean result = false;
        selected.setPassword(pfPassword.getText());
        selected.setAppPrivileges(cbPrivileges.getValue().getValue());
        selected.setActivated(checkAccountActive.isSelected());
        selected.setFirstName(tfFirstName.getText());
        selected.setLastName(tfLastName.getText());

        try {

            result = user.getUserHandler().editUser(selected);
            if (result == true) {
                FormLoader.showInformationDialog("Обавјештење", "Корисник је успјешно измјењен.");
                loadUsers();
                listViewUsers.getSelectionModel().select(userItem);
            }
            return result;
        } catch (EntityDoesNotExistException ex) {
            FormLoader.showInformationDialog("Грешка", "Корисник не постоји !");
        } catch (StoringException ex) {
            FormLoader.showInformationDialog("Грешка са базом", "Није успјелa измјена корисника !");
        }
        return result;
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
