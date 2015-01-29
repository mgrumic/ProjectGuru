/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.tasktree.TaskNode;
import projectguru.tasktree.TaskTree;
import projectguru.utils.FormLoader;

/**
 *
 * @author ZM
 */
public class TeamOfficeController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label label;

    @FXML
    private ListView<UserWrapper> listMembers;

    @FXML
    private ListView<ProjectWrapper> listProjects;

    @FXML
    private Accordion accProjects;

    @FXML
    private Accordion accMembers;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private AnchorPane leftPane;

    @FXML
    private TreeTableView<TaskNode> treeTasks;

    @FXML
    private TreeTableColumn<TaskNode, String> treeColumnTasks;

    @FXML
    private TreeTableColumn<TaskNode, String> treeColumnDescription;

    @FXML
    private Label lblTime;

    @FXML
    private AnchorPane anchorHeader;

    @FXML
    private Button btnAddSubtask;

    @FXML
    private Button btnAddActivity;

    @FXML
    private Button btnNewProject;

    @FXML
    private Button btnAddMember;

    @FXML
    private TextField tfSearchProjects;

    @FXML
    private TextField tfSearchMembers;

    @FXML
    void btnAddSubtaskPressed(ActionEvent event) {

        ProjectWrapper projectItem = listProjects.getSelectionModel().getSelectedItem();
        if (projectItem != null) {
            TreeItem<TaskNode> taskNode = null;
            if (treeTasks.getRoot() == null) {
                try {
                    FormLoader.loadFormAddTask(projectItem.getProject(), null);
                } catch (IOException ex) {
                    Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((taskNode = treeTasks.getSelectionModel().getSelectedItem()) != null) {
                try {
                    FormLoader.loadFormAddTask(projectItem.getProject(), null);
                } catch (IOException ex) {
                    Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                FormLoader.showInformationDialog("Напомена", "Изаберите задатак за који желите додати подзадатак !");
            }
        } else {
            FormLoader.showInformationDialog("Напомена", "Изаберите пројекат за који желите додати подзадатак !");
        }

    }

    @FXML
    void btnAddMemberPressed(ActionEvent event) {

    }

    @FXML
    void btnNewProjectPressed(ActionEvent event) {
        try {
            FormLoader.loadFormAddProject();
        } catch (IOException ex) {
            Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void btnAddActivityPressed(ActionEvent event) {
        if (treeTasks.getSelectionModel().getSelectedItem() != null) {
            TaskNode taskNode = treeTasks.getSelectionModel().getSelectedItem().getValue();
            try {
                FormLoader.loadFormActivities(taskNode.getTask());
            } catch (IOException ex) {
                Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FormLoader.showInformationDialog("Напомена", "Изаберите задатак не који желите додати активност !");
        }

    }
    /**
     * Moje varijable
     */
    private LoggedUser user;
    private ObservableList<ProjectWrapper> projects;
    private ObservableList<UserWrapper> members;
    private long time = System.currentTimeMillis();
    private Timeline timeline;
    private ContextMenu rootContextMenu;

    private static final ImageView rootImage = new ImageView(new Image(TeamOfficeController.class
            .getResourceAsStream("/projectguru/images/root.png")));

    @FXML
    void initialize() {

        assert label != null : "fx:id=\"label\" was not injected: check your FXML file 'TeamOffice.fxml'.";

        btnAddActivity.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/add_activity.png"))));
        btnAddSubtask.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/add_task.png"))));
        btnAddMember.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/add_member.png"))));
        btnNewProject.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/add_project.png"))));

        listProjects.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProjectWrapper>() {
            @Override
            public void changed(ObservableValue<? extends ProjectWrapper> observable, ProjectWrapper oldValue, ProjectWrapper newValue) {
                if (newValue != null) {
                    loadMembers(newValue.getProject());
                    loadTaskTree(newValue.getProject());
                }
            }
        });

        rootContextMenu = new ContextMenu();
        final MenuItem addSubtask = new MenuItem("Додај подзадатак");
        final MenuItem addActivity = new MenuItem("Додај активност");
        addSubtask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnAddSubtaskPressed(event);
            }
        });
        addActivity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnAddActivityPressed(event);
            }
        });
        rootContextMenu.getItems().addAll(addSubtask, addActivity);

        treeColumnTasks.setCellFactory(new Callback<TreeTableColumn<TaskNode, String>, TreeTableCell<TaskNode, String>>() {
            @Override
            public TreeTableCell<TaskNode, String> call(TreeTableColumn<TaskNode, String> param) {
                TreeTableCell<TaskNode, String> cell = new TreeTableCell<TaskNode, String>() {
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (bln) {
                            setText(null);
                            setContextMenu(null);
                        } else if (t != null) {
                            setText(t);
                            setContextMenu(rootContextMenu);
                        }

                    }

                };
                return cell;
            }

        });
        treeColumnTasks.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<TaskNode, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getTask().getName())
        );
        treeColumnDescription.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<TaskNode, String> param)
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getTask().getDescription())
        );
        accProjects.setExpandedPane(accProjects.getPanes().get(0));
        accMembers.setExpandedPane(accMembers.getPanes().get(0));

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                setTime();
                            }
                        }));
        timeline.playFromStart();

        tfSearchProjects.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                    Object newVal) {
                searchProjects((String) oldVal, (String) newVal);
            }
        });
        tfSearchMembers.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                    Object newVal) {
                if(listProjects.getSelectionModel().getSelectedItem() != null) {
                    searchMembers((String) oldVal, (String) newVal);
                }
            }
        });
    }

    public void setUser(LoggedUser user) {

        this.user = user;
        loadProjects();

    }

    private void loadProjects() {
        projects = FXCollections.observableArrayList(
                user.getProjectHandler().getAllProjects()
                .stream()
                .map((pr) -> new ProjectWrapper(pr))
                .collect(Collectors.toList())
        );
        listProjects.setItems(projects);
    }

    private void loadMembers(Project project) {
        members = FXCollections.observableArrayList(
                user.getProjectHandler().getAllMembers(project)
                .stream()
                .map((member) -> new UserWrapper(member))
                .collect(Collectors.toList()));
        listMembers.setItems(members);
    }

    private void loadTaskTree(Project project) {

        treeTasks.setRoot(null);
        Task rootTask = project.getIDRootTask();
        if (rootTask != null) {
            TreeItem<TaskNode> root = new TreeItem<>(new TaskNode(rootTask));
            root.setExpanded(true);
            TaskTree tree = user.getTaskHandler().getTaskTree(rootTask);
            recursiveTaskTreeLoad(root, tree.getRoot());
            treeTasks.setRoot(root);
        }
    }

    private void recursiveTaskTreeLoad(TreeItem<TaskNode> root, TaskNode task) {
        List<TaskNode> children = task.getChildren();
        children.stream().forEach((child) -> {
            TreeItem<TaskNode> newRoot = new TreeItem<>(child);
            root.getChildren().add(newRoot);
            recursiveTaskTreeLoad(newRoot, child);
        });
    }

    private void setTime() {
        long ellapsed = System.currentTimeMillis();
        ellapsed -= time;
        ellapsed /= 1000;
        long s = ellapsed % 60;
        long m = (ellapsed / 60) % 60;
        long h = (ellapsed / (60 * 60)) % 24;
        lblTime.setText(String.format("%02d:%02d:%02d", h, m, s));
    }

    private void searchProjects(String oldValue, String newValue) {
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            listProjects.setItems(projects);
        }
        if(newValue.length() == 0)
        {
            loadProjects();
            return;
        }
        String value = newValue.toUpperCase();
        ObservableList<ProjectWrapper> subentries = FXCollections.observableArrayList();
        for (Object entry : listProjects.getItems()) {
            boolean match = true;
            ProjectWrapper entryProject = (ProjectWrapper) entry;
            if (entryProject.getProject().getName().toUpperCase().startsWith(value)) {
                subentries.add(entryProject);
            }
        }
        listProjects.setItems(subentries);
    }

    private void searchMembers(String oldValue, String newValue) {
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            listMembers.setItems(members);
        }
        if(newValue.length() == 0 && listProjects.getSelectionModel().getSelectedItem() != null)
        {
            loadMembers(listProjects.getSelectionModel().getSelectedItem().getProject());
            return;
        }
        String value = newValue.toUpperCase();
        ObservableList<UserWrapper> subentries = FXCollections.observableArrayList();
        for (Object entry : listMembers.getItems()) {
            boolean match = true;
            UserWrapper entryUser = (UserWrapper) entry;
            if (entryUser.getUser().getUsername().toUpperCase().startsWith(value)) {
                subentries.add(entryUser);
            }
        }
        listMembers.setItems(subentries);
    }

    /**
     * Moje private Wrapper klase
     */
    private static class ProjectWrapper {

        private Project project;

        public ProjectWrapper(Project project) {
            this.project = project;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }

        @Override
        public String toString() {
            return project.getName();
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

    }

    private static class TaskWrapper {

        private Task task;

        public TaskWrapper(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        @Override
        public String toString() {
            return task.getName();
        }

    }

}
