/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import projectguru.controllers.util.SerbianLocalDateStringConverter;
import projectguru.entities.Document;
import projectguru.entities.DocumentRevision;
import projectguru.entities.Privileges;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
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
    private Label lblStatusLabel;
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
    private Button btnGetReport;
    @FXML
    private Button btnDocuments;
    @FXML
    private Button btnFinances;
    @FXML
    private Button btnCurrentTask;
    @FXML
    private Button btnUsersOnTask;
    
    @FXML
    void btnAddSubtaskPressed(ActionEvent event) {

        ProjectWrapper projectItem = listProjects.getSelectionModel().getSelectedItem();
        if (projectItem != null) {
            TreeItem<TaskNode> taskNode = null;
            if (treeTasks.getRoot() == null) {
                if (user.getProjectHandler().checkProjectChefPrivileges(projectItem.getProject())) {
                    try {
                        FormLoader.loadFormAddTask(projectItem.getProject(), null, user, this, false);
                    } catch (IOException ex) {
                        Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    FormLoader.showInformationDialog("Обавјештење", "Немате довољно привилегија");
                }
            } else if ((taskNode = treeTasks.getSelectionModel().getSelectedItem()) != null) {
                if (user.getTaskHandler().checkTaskChefPrivileges(taskNode.getValue().getTask())) {
                    try {
                        FormLoader.loadFormAddTask(projectItem.getProject(), taskNode.getValue().getTask(), user, this, false);
                    } catch (IOException ex) {
                        Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    FormLoader.showInformationDialog("Обавјештење", "Немате довољно привилегија");
                }
            } else {

                FormLoader.showInformationDialog("Напомена", "Изаберите задатак за који желите додати подзадатак !");
            }
        } else {
            FormLoader.showInformationDialog("Напомена", "Изаберите пројекат за који желите додати подзадатак !");
        }

    }

    private void actionOnEditTask() {
        ProjectWrapper projectItem = listProjects.getSelectionModel().getSelectedItem();
        if (projectItem != null) {
            TreeItem<TaskNode> taskNode = null;
            if (treeTasks.getRoot() == null) {
                try {
                    FormLoader.loadFormAddTask(projectItem.getProject(), null, user, this, true);
                } catch (IOException ex) {
                    Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ((taskNode = treeTasks.getSelectionModel().getSelectedItem()) != null) {
                try {
                    FormLoader.loadFormAddTask(projectItem.getProject(), taskNode.getValue().getTask(), user, this, true);
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
        try {
            FormLoader.loadFormAddMembersOnProjects(user,
                    listProjects.getSelectionModel().getSelectedItem().getProject(),
                    this
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void btnNewProjectPressed(ActionEvent event) {
        try {
            FormLoader.loadFormAddProject(user, null);
            loadProjects();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void btnEditProjectPressed(ActionEvent event) {
        ProjectWrapper projectWrapper = listProjects.getSelectionModel().getSelectedItem();
        if (projectWrapper != null) {
            Project project = projectWrapper.getProject();
            try {
                FormLoader.loadFormAddProject(user, project);
                loadProjects();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void btnAddActivityPressed(ActionEvent event) {
        if (treeTasks.getSelectionModel().getSelectedItem() != null) {
            TaskNode taskNode = treeTasks.getSelectionModel().getSelectedItem().getValue();
            try {
                FormLoader.loadFormActivities(user, taskNode.getTask());
            } catch (IOException ex) {
                Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FormLoader.showInformationDialog("Напомена", "Изаберите задатак не који желите додати активност !");
        }

    }

    @FXML
    void btnDocumentsPressed(ActionEvent event) {

        ProjectWrapper projectWrapper = listProjects.getSelectionModel().getSelectedItem();
        try {
            if (projectWrapper != null) {
                FormLoader.loadFormDocumentation(projectWrapper.getProject(), user);
            }
        } catch (IOException ex) {
            Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
        }
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
    void mItemClosePressed(ActionEvent event) {
        ((Stage) btnAddActivity.getScene().getWindow()).close();
    }

    @FXML
    void btnGetReportPressed(ActionEvent event) {
        try {
            ProjectWrapper item = listProjects.getSelectionModel().getSelectedItem();
            if (item != null) {
                FormLoader.loadFormReport(item.getProject(), user);
            } else {
                FormLoader.showInformationDialog("Обавјештење", "Изаберите пројекат за који желите генерисати извјештај");
            }
        } catch (Exception ex) {
            FormLoader.showErrorDialog("Грешка у апликацији", "Не може да отвори форму за извјештај");
        }
    }

    @FXML
    void btnFinancesPressed(ActionEvent event) {
        try {
            ProjectWrapper item = listProjects.getSelectionModel().getSelectedItem();
            if (item != null) {
                FormLoader.loadFormFinancesOverview(item.getProject(), user);
            } else {
                FormLoader.showInformationDialog("Обавјештење", "Изаберите пројекат за који желите погледати финансије");
            }
        } catch (Exception ex) {
            FormLoader.showErrorDialog("Грешка у апликацији", "Не може да отвори форму за преглед финансија");
        }
    }

    @FXML
    void btnCurrentTaskPressed(ActionEvent event) {
        if (activeTask == null) {
            FormLoader.showInformationDialog("Активни задатак", "Тренутно немате активног задатка !");
        } else {
            activeTask = user.getTaskHandler().getUpdatedTask(activeTask);
            FormLoader.showExtendedInformationDialog("Активни задатак", "Задатак: " + activeTask.getName(), activeTask.getDescription());
        }
    }

    @FXML
    void btnUsersOnTaskPressed(ActionEvent event) {
        TreeItem<TaskNode> taskNode = treeTasks.getSelectionModel().getSelectedItem();
        if (taskNode != null) {
            try {
                FormLoader.loadFormUsersOnTask(user, taskNode.getValue().getTask());
            } catch (IOException ex) {
                Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Moje varijable
     */
    private static LoggedUser user;
    private ObservableList<ProjectWrapper> projects;
    private ObservableList<UserWrapper> members;
    private ContextMenu rootContextMenu;
    private ContextMenu projectListMenu;
    private long time = System.currentTimeMillis();
    private Timeline clockTimeline;
    private Timeline activeTaskTimeline;
    private Task activeTask = null;
    //sekunde nakon kojih tajmer vrsi provjeru pozadiniskog zadatka
    private long seconds = 60;

    private static final ImageView rootImage = new ImageView(new Image(TeamOfficeController.class
            .getResourceAsStream("/projectguru/images/root.png")));

    public void updateLoggedUser() throws EntityDoesNotExistException {
        user.setUser(user.getProjectHandler().getUpdatedUser(user.getUser()));
        if (user.getUser().getActivated() == false) {
            FormLoader.showExtendedInformationDialog("Обавјештење", null, "Ваш налог је деактивиран.\nКонтактирајте администратора за више информација.");
            System.exit(0);
        }
        setGUIForUser();
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
        btnDocuments.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/documents.png"))));
        btnGetReport.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/report.png"))));
        btnFinances.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/finances.png"))));
        btnCurrentTask.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/active_task.png"))));
        btnUsersOnTask.setGraphic(new ImageView(new Image(TeamOfficeController.class
                .getResourceAsStream("/projectguru/images/members.png"))));
        
        projectListMenu = new ContextMenu();
        final MenuItem refreshProjectList = new MenuItem("Учитај све пројекте");

        refreshProjectList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadProjects();
            }
        });
        projectListMenu.getItems().add(refreshProjectList);
        listProjects.setContextMenu(projectListMenu);

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
        final MenuItem editSubtask = new MenuItem("Прикажи задатак");
        final MenuItem activateTask = new MenuItem("Aктивирај задатак");
        final MenuItem manageMembers = new MenuItem("Чланови задатка");

        addSubtask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnAddSubtaskPressed(event);
            }
        });
        editSubtask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                btnAddSubtaskPressed(event);
                actionOnEditTask();
            }
        });
        addActivity.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnAddActivityPressed(event);
            }
        });
        activateTask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<TaskNode> taskNode = treeTasks.getSelectionModel().getSelectedItem();
                if (taskNode != null) {
                    try {
                        FormLoader.loadFormSetActiveTask(taskNode.getValue().getTask(), user);
                    } catch (IOException ex) {
                        Logger.getLogger(TeamOfficeController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        manageMembers.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btnUsersOnTaskPressed(event);
            }
        });
        rootContextMenu.getItems().addAll(addSubtask, addActivity, editSubtask, activateTask, manageMembers);

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
                            TaskNode node = this.getTreeTableRow().getItem();
                            if (node != null && user.getTaskHandler().checkTaskChefPrivileges(node.getTask())) {
                                setContextMenu(rootContextMenu);
                            }
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
                            setText(String.format("%5.2f", t * 100) + "%");
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
                -> new ReadOnlyStringWrapper(param.getValue().getValue().getTask().getDescription().replaceAll("\n", " "))
        );
        treeColumnCompleted.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<TaskNode, Double> param)
                -> new ReadOnlyObjectWrapper<Double>(param.getValue().getValue().getPartDone()));

        accProjects.setExpandedPane(accProjects.getPanes().get(0));
        accMembers.setExpandedPane(accMembers.getPanes().get(0));

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

        /**
         * Dio zaduzen za satnicu
         */
        clockTimeline = new Timeline();
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                setTime();
                            }
                        }));
        activeTaskTimeline = new Timeline();
        activeTaskTimeline.setCycleCount(Timeline.INDEFINITE);
        activeTaskTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(60),
                        new EventHandler() {
                            @Override
                            public void handle(Event event) {
                                checkActiveTask();
                            }
                        }));
    }

    public void setGUIForUser() {
        int privileges = user.getUser().getAppPrivileges();
        if (privileges == Privileges.NO_PRIVILEGES.ordinal()) {
            btnNewProject.setDisable(true);
            mItemKorisnickiNalozi.setVisible(false);
        } else if (privileges == Privileges.CHEF.ordinal()) {
            btnNewProject.setDisable(false);
            mItemKorisnickiNalozi.setVisible(false);
        } else if (privileges == Privileges.ADMIN.ordinal()) {
            btnNewProject.setDisable(false);
            mItemKorisnickiNalozi.setVisible(true);
        }
    }

    public void setUser(LoggedUser user) {
        this.user = user;
    }

    public void load() {
        setGUIForUser();
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
        if (projects.size() > 0) {
            listProjects.getSelectionModel().select(null);
        }
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

    public void addNodeToTree(Task subtask) {
        TreeItem<TaskNode> task = treeTasks.getSelectionModel().getSelectedItem();
        TreeItem<TaskNode> node = new TreeItem<>(user.getTaskHandler().getTaskTree(subtask).getRoot());
        if (task == null || treeTasks.getRoot() == null) {
            treeTasks.setRoot(node);
        } else {

            task.getChildren().add(node);
        }
        ProjectWrapper project = listProjects.getSelectionModel().getSelectedItem();
        loadProjects();
        if (project != null && projects.contains(project)) {
            listProjects.getSelectionModel().select(project);
        }
    }

    private void restartClock() {
        time = System.currentTimeMillis();
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
                        chartPie.setTitle(String.format("Укупно одрађено: %5.2f ", (root.getValue().getPartDone() * 100)) + "%");
                        worked = new PieChart.Data("Одрађено", root.getValue().getPartDone() * 100);
                        left = new PieChart.Data("Преостало", 100 - (root.getValue().getPartDone() * 100));
                    } else {
                        chartPie.setTitle("Укупно одрађено: 0%");
                        worked = new PieChart.Data("Одрађено", 0);
                        left = new PieChart.Data("Преостало", 100);
                    }
                    pieChartData.add(worked);
                    pieChartData.add(left);
                    chartPie.setData(pieChartData);

                    Project project = projectWrapper.getProject();
                    lblStatusLabel.setText("Тренутно изабрани пројекат: " + project.getName());
                    lblProjectName.setText(project.getName());
                    tAreaDescription.setText(project.getDescription());
                    LocalDate start = project.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    SerbianLocalDateStringConverter cnv = new SerbianLocalDateStringConverter();
                    lblStartDate.setText(cnv.toString(start));
                    LocalDate end = project.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    lblEndDate.setText(cnv.toString(end));
                    lblBudget.setText(String.format("%02d", project.getBudget().intValue()));
                    lblNumTasks.setText((root != null) ? (root.getValue().getTask().getClosureTasksChildren().size() + 1) + "" : "0");
                    lblNumMembers.setText(project.getWorksOnProjectList().size() + "");
                    try {
                        lblNumActivities.setText((root != null) ? (user.getActivityHandler().findActivitiesForTask(root.getValue().getTask(), true, false)).size() + "" : "0");
                    } catch (InsuficientPrivilegesException ex) {
                        lblNumActivities.setText("- ");
                    } catch (StoringException ex) {
                        lblNumActivities.setText("- ");
                    }
                    ObservableList<UserWrapper> userList = FXCollections.observableArrayList(
                            user.getProjectHandler().getAllChefs(project)
                            .stream()
                            .map((member) -> new UserWrapper(member))
                            .collect(Collectors.toList()));
                    listChefs.setItems(userList);
                    ProjectHandler ph = user.getProjectHandler();
                    if (ph.checkProjectChefPrivileges(project)) {
                        btnAddMember.setDisable(false);
                        btnAddSubtask.setVisible(true);
                        btnAddActivity.setVisible(true);
                        btnEditProject.setVisible(true);
                        btnFinances.setVisible(true);
                        btnDocuments.setVisible(true);
                        btnGetReport.setVisible(true);
                        btnUsersOnTask.setVisible(true);
                        lblProjectCompleted.setText("Шеф пројекта");
                    } else if (ph.checkMemberPrivileges(project)) {
                        btnAddMember.setDisable(true);
                        btnAddSubtask.setVisible(true);
                        btnAddActivity.setVisible(true);
                        btnEditProject.setVisible(false);
                        btnFinances.setVisible(true);
                        btnDocuments.setVisible(true);
                        btnGetReport.setVisible(true);
                        btnUsersOnTask.setVisible(false);
                        lblProjectCompleted.setText("Члан пројекта");
                    } else if (ph.checkInsightPrivileges(project)) {
                        btnAddMember.setDisable(true);
                        btnAddSubtask.setVisible(false);
                        btnAddActivity.setVisible(false);
                        btnEditProject.setVisible(false);
                        btnFinances.setVisible(true);
                        btnDocuments.setVisible(true);
                        btnGetReport.setVisible(true);
                        btnUsersOnTask.setVisible(false);
                        lblProjectCompleted.setText("Надзор");
                    } else if (ph.checkExternPrivileges(project)) {
                        btnAddMember.setDisable(true);
                        btnAddSubtask.setVisible(false);
                        btnAddActivity.setVisible(false);
                        btnEditProject.setVisible(false);
                        btnFinances.setVisible(false);
                        btnDocuments.setVisible(true);
                        btnGetReport.setVisible(true);
                        btnUsersOnTask.setVisible(false);
                        lblProjectCompleted.setText("Екстерни члан");
                    } else {
                        btnAddMember.setDisable(true);
                        btnAddSubtask.setVisible(false);
                        btnAddActivity.setVisible(false);
                        btnEditProject.setVisible(false);
                        btnFinances.setVisible(false);
                        btnGetReport.setVisible(false);
                        btnDocuments.setVisible(false);
                        btnUsersOnTask.setVisible(false);
                        lblProjectCompleted.setText("Немате привилегија");
                    }
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
     * Active task stuff
     */
    public void startActiveTask() {
        activeTask = user.getTaskHandler().getActiveTask();
        if (activeTask == null) {
            FormLoader.showExtendedInformationDialog("Обавјештење", " ", "Тренутно немате активни задатак.\n"
                    + "Поновна провјера се врши за " + seconds / 60 + " мин.\n"
                    + "Уколико добијете задатак почеће вам се рачунати вријеме.\n"
                    + "За више информација кликните на дугме за активни задатак.\n");
        } else {
            clockTimeline.playFromStart();
            FormLoader.showExtendedInformationDialog("Обавјештење", " ", "Почео је ваш рад на активном задатку.\n"
                    + "За више информација кликните на дугме за активни задатак.\n");
            try {
                user.getTaskHandler().createNewTimetableEntry(new Date(), new Date());
            } catch (StoringException ex) {
                FormLoader.showErrorDialog("Грешка", "Грешка приликом уписа нове сатнице у базу.");
            }
        }
        activeTaskTimeline.playFromStart();
    }

    private void checkActiveTask() {
        try {
            updateLoggedUser();
        } catch (EntityDoesNotExistException ex) {

        }
        Task check = user.getTaskHandler().getActiveTask();
        if (check == null) {
            if (activeTask != null) {
                activeTask = check;
                clockTimeline.stop();
                FormLoader.showExtendedInformationDialog("Обавјештење", " ", "Тренутно немате активни задатак.\n"
                        + "Поновна провјера се врши за " + seconds / 60 + " мин.\n"
                        + "За више информација кликните на дугме за активни задатак.\n");
            }
        } else {
            if (activeTask == null) {
                restartClock();
                clockTimeline.playFromStart();
                FormLoader.showExtendedInformationDialog("Обавјештење", " ", "Почео је ваш рад на активном задатку.\n"
                        + "За више информација кликните на дугме за активни задатак.\n");
                try {
                    user.getTaskHandler().createNewTimetableEntry(new Date(), new Date());
                } catch (StoringException ex) {
                    FormLoader.showErrorDialog("Грешка", "Грешка приликом уписа нове сатнице у базу.");
                }
            } else if (!check.getId().equals(activeTask.getId())) {
                restartClock();
                clockTimeline.playFromStart();
                FormLoader.showExtendedInformationDialog("Обавјештење", " ", "Дошло је до промјене вашег активног задатка.\n"
                        + "За више информација кликните на дугме за активни задатак.");
                try {
                    user.getTaskHandler().createNewTimetableEntry(new Date(), new Date());
                } catch (StoringException ex) {
                    FormLoader.showErrorDialog("Грешка", "Грешка приликом уписа нове сатнице у базу.");
                }
            } else {
                try {
                    user.getTaskHandler().updateActiveTime(new Date());
                } catch (StoringException ex) {
                    FormLoader.showErrorDialog("Грешка", "Грешка приликом продужења сатнице у бази.");
                }
            }
        }
        activeTask = check;
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

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ProjectWrapper other = (ProjectWrapper) obj;
            if (!project.getId().equals(other.getProject().getId())) {
                return false;
            }
            return true;
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

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof UserWrapper)) {
                return false;
            }
            UserWrapper other = (UserWrapper) object;
            return user.equals(other.getUser());
        }

    }

    public static class DocumentWrapper {

        private Document document;

        public DocumentWrapper(Document document) {
            this.document = document;
        }

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }

        @Override
        public String toString() {
            return document.getName();
        }
    }

    public static class DocumentRevisionWrapper {

        private DocumentRevision document;

        public DocumentRevisionWrapper(DocumentRevision document) {
            this.document = document;
        }

        public DocumentRevision getDocument() {
            return document;
        }

        public void setDocument(DocumentRevision document) {
            this.document = document;
        }

        @Override
        public String toString() {
            return document.getDatePosted().toString();
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
