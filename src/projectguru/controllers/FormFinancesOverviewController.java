/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import projectguru.entities.Project;
import projectguru.handlers.LoggedUser;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.utils.FormLoader;
/**
 * FXML Controller class
 *
 * @author ZM
 */
public class FormFinancesOverviewController implements Initializable {

    @FXML
    private Label lblActivityExpenses;

    @FXML
    private Label lblTotalIncomes;

    @FXML
    private Label lblActivityIncomes;

    @FXML
    private Label lblBudget;

    @FXML
    private Label lblTotalExpenses;

    @FXML
    private Button btnIncomes;

    @FXML
    private Button btnExpenses;

    @FXML
    private Label lblProjectName;
    
    @FXML
    void btnIncomesPressed(ActionEvent event) {
        try {
            FormLoader.loadFormAddExpenseOrIncome(project, null, this, user, true);
        } catch (IOException ex) {
            FormLoader.showErrorDialog("Грешка", "Грешка током учитавања форме");
        }
    }

    @FXML
    void btnExpensesPressed(ActionEvent event) {
        try {
            FormLoader.loadFormAddExpenseOrIncome(project, null, this, user, false);
        } catch (IOException ex) {
            FormLoader.showErrorDialog("Грешка", "Грешка током учитавања форме");
        }
    }
    /**
     * Moje varijable
     */
    private LoggedUser user;
    private Project project;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setProjectAndUser(Project project, LoggedUser user) {
        this.project = project;
        this.user = user;
    }
    public void load(){
        if(user != null && project != null){
            loadFinances();
            if(user.getProjectHandler().checkProjectChefPrivileges(project)){
                btnIncomes.setVisible(true);
                btnExpenses.setVisible(true);
            }else{
                btnIncomes.setVisible(false);
                btnExpenses.setVisible(false);
            }
        }
    }
    public void loadFinances(){

        try {
            project = user.getProjectHandler().getUpdatedProject(project);
        } catch (EntityDoesNotExistException ex) {
            FormLoader.showErrorDialog("Грешка", "Такав пројекат не постоји");
            ((Stage)lblBudget.getScene().getWindow()).close();
        }
        
        lblProjectName.setText(project.getName());
        lblBudget.setText(project.getBudget().toString());
        double result = 0;
        for(Income inc : project.getIncomeList()){
            result += inc.getMoneyAmmount().doubleValue();
        }
        lblTotalIncomes.setText(String.format("%.02f", result));
        result = 0;
        for(Expense exp : project.getExpenseList()){
            result += exp.getMoneyAmmount().doubleValue();
        }
        lblTotalExpenses.setText(String.format("%.02f", result));
        result = 0;
        for(Income inc : project.getIncomeList()){
            if(inc.getIDActivity() != null){
                result += inc.getMoneyAmmount().doubleValue();
            }
        }
        lblActivityIncomes.setText(String.format("%.02f", result));
        result = 0;
        for(Expense exp : project.getExpenseList()){
            if(exp.getIDActivity() != null){
                result += exp.getMoneyAmmount().doubleValue();
            }
        }
        lblActivityExpenses.setText(String.format("%.02f", result));
    }
    
}
