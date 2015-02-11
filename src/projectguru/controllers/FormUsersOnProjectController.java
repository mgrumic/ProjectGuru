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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projectguru.entities.Privileges;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.entities.WorksOnProject;
import projectguru.entities.WorksOnTask;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.utils.FormLoader;

/**
 * FXML Controller class
 *
 * @author ZM
 */
public class FormUsersOnProjectController implements Initializable {

    @FXML
    private ListView<UserWrapper> listMembers;

    @FXML
    private ComboBox<ProjectPrivileges> cbPrivileges;

    @FXML
    private TextField tfLastName;

    @FXML
    private TextField tfUsername;

    @FXML
    private TextField tfFirstName;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnChangePrivileges;

    @FXML
    void btnChangePrivilegesPressed(ActionEvent event) {
        UserWrapper item = listMembers.getSelectionModel().getSelectedItem();
        if (item != null) {
            User member = item.getUser();
            int privileges = cbPrivileges.getValue().getValue().ordinal();
            if (privileges == Privileges.NO_PRIVILEGES.ordinal()
                    || privileges == Privileges.EXTERN.ordinal() || privileges == Privileges.INSIGHT.ordinal()) {
                try {
                    for (WorksOnTask wot : member.getWorksOnTaskList()) {
                        Task task = user.getTaskHandler().getUpdatedTask(wot.getTask());
                        user.getTaskHandler().removeMember(task, member);
                        user.getProjectHandler().setPrivileges(project, member, cbPrivileges.getValue().getValue());
                    }
                    user.getProjectHandler().setPrivileges(project, member, cbPrivileges.getValue().getValue());
                    FormLoader.showInformationDialog("Обавјештење", "Успјешна промјена привилегија");
                } catch (StoringException ex) {
                   FormLoader.showErrorDialog("Грешка", "Грешка са базом");
                } catch (InsuficientPrivilegesException ex) {
                    FormLoader.showErrorDialog("Грешка", "Немате довољно привилегија");
                } catch (EntityDoesNotExistException ex) {
                    FormLoader.showErrorDialog("Грешка", "Објекат не постоји у бази");
                }
            } else {
                try {
                    user.getProjectHandler().setPrivileges(project, member, cbPrivileges.getValue().getValue());
                    FormLoader.showInformationDialog("Обавјештење", "Успјешна промјена привилегија");
                } catch (StoringException ex) {
                    FormLoader.showErrorDialog("Грешка", "Грешка са базом");
                }
            }
        }
    }

    @FXML
    void btnExitPressed(ActionEvent event) {
        ((Stage) btnExit.getScene().getWindow()).close();
    }

    /**
     * Moje varijable
     */
    private Project project;
    private LoggedUser user;
    private ObservableList<UserWrapper> members;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listMembers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<UserWrapper>() {
            @Override
            public void changed(ObservableValue<? extends UserWrapper> observable, UserWrapper oldValue, UserWrapper newValue) {
                if (newValue != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            changeFields();
                        }
                    });
                }
            }
        });

        ContextMenu menu = new ContextMenu();
        MenuItem refresh = new MenuItem("Учитај чланове");
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadMembers();
            }
        });
        menu.getItems().add(refresh);

        listMembers.setContextMenu(menu);

        cbPrivileges.getItems().add(new ProjectPrivileges("Без привилегија", Privileges.NO_PRIVILEGES));
        cbPrivileges.getItems().add(new ProjectPrivileges("Екстерни члан", Privileges.EXTERN));
        cbPrivileges.getItems().add(new ProjectPrivileges("Надзор", Privileges.INSIGHT));
        cbPrivileges.getItems().add(new ProjectPrivileges("Члан пројекта", Privileges.MEMBER));
        cbPrivileges.getItems().add(new ProjectPrivileges("Шеф пројекта", Privileges.CHEF));

        cbPrivileges.getSelectionModel().select(0);
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setLoggedUser(LoggedUser loggedUser) {
        this.user = loggedUser;
    }

    public void loadMembers() {
        if (project != null) {
            try {
                project = user.getProjectHandler().getUpdatedProject(project);
            } catch (EntityDoesNotExistException ex) {
            }
            members = FXCollections.observableArrayList(
                    user.getProjectHandler().getAllMembers(project)
                    .stream()
                    .map((member) -> new UserWrapper(member))
                    .collect(Collectors.toList()));
            listMembers.setItems(members);
            listMembers.getSelectionModel().select(null);
        }
    }

    private void changeFields() {
        UserWrapper item = listMembers.getSelectionModel().getSelectedItem();
        if (item != null) {
            User member = item.getUser();
            tfFirstName.setText(member.getFirstName());
            tfLastName.setText(member.getLastName());
            tfUsername.setText(member.getUsername());
            int privileges = 0;
            for (WorksOnProject wop : member.getWorksOnProjectList()) {
                if (wop.getWorksOnProjectPK().getIDProject() == project.getId()) {
                    privileges = wop.getPrivileges();
                }
            }
            setComboBoxValue(privileges);
        }
    }

    private void setComboBoxValue(int privileges) {
        for (ProjectPrivileges pp : cbPrivileges.getItems()) {
            if (pp.getValue().ordinal() == privileges) {
                cbPrivileges.getSelectionModel().select(pp);
                break;
            }
        }
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

    private static class ProjectPrivileges {

        private String naziv;
        private Privileges value;

        public ProjectPrivileges(String naziv, Privileges value) {
            this.naziv = naziv;
            this.value = value;
        }

        public String getNaziv() {
            return naziv;
        }

        public Privileges getValue() {
            return value;
        }

        public void setValue(Privileges value) {
            this.value = value;
        }

        public void setNaziv(String naziv) {
            this.naziv = naziv;
        }

        @Override
        public String toString() {
            return naziv;
        }

    }
}
