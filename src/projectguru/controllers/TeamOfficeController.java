/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import projectguru.entities.Privileges;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
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
    private TreeTableColumn<TaskNode, Double> treeColumnCompleted;

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
    private Button btnEditProject;
    @FXML
    private TextField tfSearchProjects;
    @FXML
    private TextField tfSearchMembers;

    @FXML
    private Label lblProjectName;
    @FXML
    private Label lblProjectCompleted;
    @FXML
    private Label lblStartDate;
    @FXML
    private TextArea tAreaDescription;
    @FXML
    private Label lblNumMembers;
    @FXML
    private Label lblEndDate;
    @FXML
    private Label lblBudget;
    @FXML
    private Label lblNumActivities;
    @FXML
    private Label lblNumTasks;
    @FXML
    private PieChart chartPie;
    @FXML
    private ListView<UserWrapper> listChefs;
    @FXML
    private MenuItem mItemKorisnickiNalozi;
    @FXML
    private Button getReportTest;

    @FXML
    void btnAddSubtaskPressed(ActionEvent event) {

        ProjectWrapper projectItem = listProjects.getSelectionModel().getSelectedItem();
        if (projectItem != null) {
            TreeItem<TaskNode> taskNode = null;
            if (treeTasks.getRoot() == null) {
                try {
                    FormLoader.loadFormAddTask(projectItem.getProject(), null, user);
                } catch (IOException ex) {
                    Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((taskNode = treeTasks.getSelectionModel().getSelectedItem()) != null) {
                try {
                    FormLoader.loadFormAddTask(projectItem.getProject(), null, user);
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
            FormLoader.loadFormAddProject(user, this);
        } catch (IOException ex) {

        }
    }

    @FXML
    void btnEditProjectPressed(ActionEvent event) {
        ProjectWrapper projectWrapper = listProjects.getSelectionModel().getSelectedItem();
        if (projectWrapper != null) {
            Project project = projectWrapper.getProject();

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

    @FXML
    void btnDokumPressed(ActionEvent event){
        
        ProjectWrapper projectWrapper = listProjects.getSelectionModel().getSelectedItem();
        try{
            if(projectWrapper != null){
                FormLoader.loadFormDocumentation(projectWrapper.getProject(), user);
            }
        }catch(IOException ex){
            Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE,null, ex);
        }
        catch(Exception e){}
    }
    @FXML
    void mItemKorisnickNaloziPressed(ActionEvent event) {
        try {
            FormLoader.loadFormUserAccounts(user);
        } catch (IOException ex) {
            Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @FXML
    void btnGetReportPressed(ActionEvent event){
        try {
            FormLoader.loadFormReport();
        } catch (Exception ex) {
            
        }
    }
    /**
     * Moje varijable
     */
    private static LoggedUser user;
    private ObservableList<ProjectWrapper> projects;
    private ObservableList<UserWrapper> members;
    private long time = System.currentTimeMillis();
    private Timeline timeline;
    private ContextMenu rootContextMenu;

    private static final ImageView rootImage = new ImageView(new Image(TeamOfficeController.class
            .getResourceAsStream("/projectguru/images/root.png")));

    public static void updateLoggedUser() throws EntityDoesNotExistException {
        user.setUser(user.getProjectHandler().getUpdatedUser(user.getUser()));
    }

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
        btnEditProject.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/edit.png"))));
        listProjects.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ProjectWrapper>() {
            @Override
            public void changed(ObservableValue<? extends ProjectWrapper> observable, ProjectWrapper oldValue, ProjectWrapper newValue) {
                if (newValue != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            loadMembers(newValue.getProject());
                            loadTaskTree(newValue.getProject());
                            setOverviewTab();
                        }
                    });
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
        treeColumnCompleted.setCellFactory(new Callback<TreeTableColumn<TaskNode, Double>, TreeTableCell<TaskNode, Double>>() {
            @Override
            public TreeTableCell<TaskNode, Double> call(TreeTableColumn<TaskNode, Double> param) {
                TreeTableCell<TaskNode, Double> cell = new TreeTableCell<TaskNode, Double>() {
                    @Override
                    protected void updateItem(Double t, boolean bln) {
                        super.updateItem(t, bln);
                        if (bln) {
                            setText(null);
                            setGraphic(null);
                        } else if (t != null) {
                            setAlignment(Pos.CENTER);
                            setText(t * 100 + "%");
                            setGraphic(new ColoredProgressBar(t));
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
        treeColumnCompleted.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<TaskNode, Double> param)
                -> new ReadOnlyObjectWrapper<Double>(param.getValue().getValue().getPartDone()));

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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        searchProjects((String) oldVal, (String) newVal);
                    }
                });
            }
        });
        tfSearchMembers.textProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldVal,
                    Object newVal) {
                if (listProjects.getSelectionModel().getSelectedItem() != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            searchMembers((String) oldVal, (String) newVal);
                        }
                    });
                }
            }
        });
    }

    public void setUser(LoggedUser user) {

        this.user = user;

        if (user.getUser().getAppPrivileges() != Privileges.ADMIN.ordinal()) {
            mItemKorisnickiNalozi.setVisible(false);
        }
        loadProjects();
        listProjects.getSelectionModel().select(0);

    }

    public void loadProjects() {
        projects = FXCollections.observableArrayList(
                user.getProjectHandler().getAllProjects()
                .stream()
                .map((pr) -> new ProjectWrapper(pr))
                .collect(Collectors.toList())
        );
        listProjects.setItems(projects);
    }

    public void loadMembers(Project project) {
        members = FXCollections.observableArrayList(
                user.getProjectHandler().getAllMembers(project)
                .stream()
                .map((member) -> new UserWrapper(member))
                .collect(Collectors.toList()));
        listMembers.setItems(members);
    }

    public void loadTaskTree(Project project) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                treeTasks.setRoot(null);
                Task rootTask = project.getIDRootTask();
                if (rootTask != null) {
                    TaskTree tree = user.getTaskHandler().getTaskTree(rootTask);
                    TreeItem<TaskNode> root = new TreeItem<>(tree.getRoot());
                    root.setExpanded(true);
                    recursiveTaskTreeLoad(root, tree.getRoot());
                    treeTasks.setRoot(root);

                }
            }
        });
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

    private void setOverviewTab() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                ProjectWrapper projectWrapper = listProjects.getSelectionModel().getSelectedItem();
                TreeItem<TaskNode> root = treeTasks.getRoot();
                if (projectWrapper != null) {
                    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                    PieChart.Data worked;
                    PieChart.Data left;
                    if (root != null) {
                        lblProjectCompleted.setText(root.getValue().getPartDone() * 100 + "%");
                        chartPie.setTitle("Укупно одрађено: " + root.getValue().getPartDone() * 100 + "%");
                        worked = new PieChart.Data("Одрађено", root.getValue().getPartDone() * 100);
                        left = new PieChart.Data("Преостало", 100 - (root.getValue().getPartDone() * 100));
                    } else {
                        lblProjectCompleted.setText("0.0 %");
                        chartPie.setTitle("Укупно одрађено: 0.0 %");
                        worked = new PieChart.Data("Одрађено", 0);
                        left = new PieChart.Data("Преостало", 100);
                    }
                    pieChartData.add(worked);
                    pieChartData.add(left);
                    chartPie.setData(pieChartData);

                    SimpleDateFormat formater = new SimpleDateFormat("YYYY-MM-DD");
                    Project project = projectWrapper.getProject();
                    lblProjectName.setText(project.getName());
                    tAreaDescription.setText(project.getDescription());
                    lblStartDate.setText(formater.format(project.getStartDate()));
                    lblEndDate.setText(formater.format(project.getEndDate()));
                    lblNumMembers.setText(project.getWorksOnProjectList().size() + "");
                    lblBudget.setText(String.format("%02d", project.getBudget().intValue()));
                    lblNumTasks.setText((root != null) ? (root.getValue().getTask().getClosureTasksChildren().size() + 1) + "" : "0");
                    lblNumActivities.setText("");
                    ObservableList<UserWrapper> userList = FXCollections.observableArrayList(
                            user.getProjectHandler().getAllChefs(project)
                            .stream()
                            .map((member) -> new UserWrapper(member))
                            .collect(Collectors.toList()));
                    listChefs.setItems(userList);
                }
            }
        });
    }

    private void searchProjects(String oldValue, String newValue) {
        if (oldValue != null && (newValue.length() < oldValue.length())) {
            listProjects.setItems(projects);
        }
        if (newValue.length() == 0) {
            loadProjects();
            return;
        }
        String value = newValue.toUpperCase();
        ObservableList<ProjectWrapper> subentries = FXCollections.observableArrayList();
        for (Object entry : listProjects.getItems()) {
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
        if (newValue.length() == 0 && listProjects.getSelectionModel().getSelectedItem() != null) {
            loadMembers(listProjects.getSelectionModel().getSelectedItem().getProject());
            return;
        }
        String value = newValue.toUpperCase();
        ObservableList<UserWrapper> subentries = FXCollections.observableArrayList();
        for (Object entry : listMembers.getItems()) {
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
            return user.getUsername();
        }

    }

    private static class ColoredProgressBar extends ProgressBar {

        public ColoredProgressBar(double initValue) {
            super(initValue);
            String cssClass = "green-bar";
            if (initValue < 0.33) {
                cssClass = "red-bar";
            } else if (initValue >= 0.33 && initValue < 0.66) {
                cssClass = "blue-bar";
            } else if (initValue >= 0.66 && initValue < 1) {
                cssClass = "green-bar";
            }
            getStyleClass().add(cssClass);
        }
    }
}
