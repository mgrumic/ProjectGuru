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
import javafx.stage.Stage;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.handlers.exceptions.UserNotTaskMemberException;
import projectguru.utils.FormLoader;

public class FormListAddMembersOnProjectController {

    @FXML
    private ListView<UserWrapper> listMembers;

    @FXML
    private Button btnAddMember;

    @FXML
    private Button btnExit;

    @FXML
    void btnAddMemberPressed(ActionEvent event) {
        UserWrapper item = listMembers.getSelectionModel().getSelectedItem();
        if (item != null) {
            try {
                boolean result = user.getProjectHandler().addMember(project, item.getUser());
                if (result) {
                    FormLoader.showInformationDialog("Обавјештење", "Корисник успјешно додан");
                } else {
                    FormLoader.showInformationDialog("Обавјештење", "Коринсик није успјњшно додан");
                }
                loadMembers();
            } catch (StoringException ex) {
                FormLoader.showErrorDialog("Грешка", "Грешка са базом");
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

    @FXML
    void initialize() {
        assert listMembers != null : "fx:id=\"listMembers\" was not injected: check your FXML file 'FormSetActiveTask.fxml'.";
    }

    public void setUserAndProject(LoggedUser user, Project project) {
        this.project = project;
        this.user = user;
    }

    public void loadMembers() {
        if (project != null && user != null) {
            members = FXCollections.observableArrayList(
                    user.getUserHandler().getAllUsers()
                    .stream()
                    .map((member) -> new UserWrapper(member))
                    .collect(Collectors.toList())
            );
            members.removeAll(FXCollections.observableArrayList(
                    user.getProjectHandler().getAllMembers(project)
                    .stream()
                    .map((member) -> new UserWrapper(member))
                    .collect(Collectors.toList()))
            );
            listMembers.setItems(members);
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
