/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ContextMenuBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.tasktree.TaskNode;
import projectguru.tasktree.TaskTree;

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
        final MenuItem addSubtask = new MenuItem("Add subtask");
        final MenuItem addActivity = new MenuItem("Add activity");
        addSubtask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });
        addActivity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

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
