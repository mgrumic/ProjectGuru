/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import projectguru.entities.Project;
import projectguru.handlers.LoggedUser;
import projectguru.jpa.handlers.JpaReportHandler;
import projectguru.utils.FormLoader;
import projectguru.utils.ReportType;
/**
 * FXML Controller class
 *
 * @author medlan
 */
public class FormReportController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private Project project;
    private LoggedUser user;
    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button generateButton;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LoggedUser getUser() {
        return user;
    }

    public void setUser(LoggedUser user) {
        this.user = user;
    }
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        reportTypeComboBox.getItems().add(ReportType.STANJE_PROJEKTA_REPORT.getText());
        reportTypeComboBox.getItems().add(ReportType.FINANSIJSKI_PREGLED_PRIHODA_REPORT.getText());
        reportTypeComboBox.getItems().add(ReportType.FINANSIJSKI_PREGLED_RASHODA_REPORT.getText());
        reportTypeComboBox.getItems().add(ReportType.PREGLED_AKTIVNOSTI_REPORT.getText());
    }
    
    @FXML
    void cancelButtonPressed(ActionEvent evt){
        ((Stage)cancelButton.getScene().getWindow()).close();
    }
    
    @FXML 
    void generateButtonPressed(ActionEvent evt){
        JpaReportHandler report = new JpaReportHandler(user);
        ReportType type = ReportType.getType(reportTypeComboBox.getValue());
        if(type == ReportType.NONE){
            FormLoader.showErrorDialog("Извјештаји", "Одаберите извјештај који требате генерисати.");
        }else{
            report.setProject(project);
            report.generateReport(type);
            report.showReport();
        }
    }
}
