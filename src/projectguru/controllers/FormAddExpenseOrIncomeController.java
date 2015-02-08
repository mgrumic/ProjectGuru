/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.math.BigDecimal;
import java.net.URL;
import javafx.fxml.FXML;
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
import javafx.fxml.Initializable;
import projectguru.entities.Activity;
import projectguru.entities.Project;
import projectguru.handlers.LoggedUser;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.handlers.exceptions.StoringException;
import projectguru.utils.FormLoader;

/**
 * FXML Controller class
 *
 * @author ZM
 */
public class FormAddExpenseOrIncomeController implements Initializable {

    @FXML
    private TextArea taDescription;

    @FXML
    private Button btnAdd;

    @FXML
    private ListView<FinanceWrapper> listFinances;

    @FXML
    private Label lblFinances;

    @FXML
    private TextField tfAmount;

    @FXML
    private Button btnExit;

    @FXML
    private ComboBox<String> cbCurrency;

    @FXML
    private Button btnNew;
    
    @FXML
    void btnNewPressed(ActionEvent event) {
        tfAmount.setText("");
        taDescription.setText("");
        
        btnAdd.setVisible(true);
    }
    
    @FXML
    void btnAddPressed(ActionEvent event) {
        if (checkFields()) {
            addFinance();
        }
    }

    @FXML
    void btnExitPressed(ActionEvent event) {
        ((Stage) btnExit.getScene().getWindow()).close();
    }

    /**
     * Moje varijable
     */
    private LoggedUser user;
    private Project project;
    private boolean type;
    private Activity activity;
    private ObservableList<FinanceWrapper> items;
    //da se refresuju labele ukoliko je pokrenuta FormOverviewFinance forma
    private FormFinancesOverviewController controller; 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /**
         * Stavo sam da aplikacija radi samo sa KM jer bi se onda uslozilo
         * racunanje ukupne sume prihoda i trskova. Sa comboboxom je ostavljena
         * mogucnost da se lako prosiri da se dodaju i druge valute
         */
        cbCurrency.getItems().add("KM");
        cbCurrency.getSelectionModel().select("KM");

        listFinances.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<FinanceWrapper>() {
            @Override
            public void changed(ObservableValue<? extends FinanceWrapper> observable, FinanceWrapper oldValue, FinanceWrapper newValue) {
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
        
        btnAdd.setVisible(false);
    }

    public void setProjectAndActivity(Project project, Activity activity) {
        this.project = project;
        this.activity = activity;
    }

    public void setUser(LoggedUser user) {
        this.user = user;
    }

    public void setType(boolean type) {
        this.type = type;
        if (type == true) {
            lblFinances.setText("Приходи:");
        } else {
            lblFinances.setText("Трошкови:");
        }
        loadData();
    }

    public void setOverviewController(FormFinancesOverviewController controller){
        this.controller = controller;
    }
    private void loadData() {
        items = FXCollections.observableArrayList();
        if (type == true) {
            for (Income inc : project.getIncomeList()) {
                items.add(new FinanceWrapper(inc, type));
            }
            listFinances.setItems(items);
        } else {
            for (Expense exp : project.getExpenseList()) {
                items.add(new FinanceWrapper(exp, type));
            }
            listFinances.setItems(items);
        }
        tfAmount.setText("");
        taDescription.setText("");
    }

    private void changeFields(FinanceWrapper item) {
        if (type == true) {
            Income inc = (Income) item.getFinance();
            tfAmount.setText(inc.getMoneyAmmount().toString());
            taDescription.setText(inc.getDescription());
        } else {
            Expense exp = (Expense) item.getFinance();
            tfAmount.setText(exp.getMoneyAmmount().toString());
            taDescription.setText(exp.getDescription());
        }
        btnAdd.setVisible(false);
    }

    private boolean checkFields() {
        boolean result = true;
        if (tfAmount.getText().equals("")) {
            result = false;
            FormLoader.showInformationDialog("Обавјештење", "Нисте унијели износ");
        } else if (taDescription.getText().length() < 15) {
            result = false;
            FormLoader.showInformationDialog("Обавјештење", "Опис мора бити дужи од 15 карактера");
        }
        return result;
    }

    private void addFinance() {
        if (type == true) {
            Income income = new Income();
            income.setMoneyAmmount(BigDecimal.valueOf(Double.parseDouble(tfAmount.getText())));
            income.setCurrency(cbCurrency.getValue());
            income.setDescription(taDescription.getText());
            income.setIDProject(project);
            String title = "Упозорење";
            String message = "Јесте ли сигурни да желите додати приход на пројекат ?";
            if (activity != null) {
                income.setIDActivity(activity);
                title = "Упозорење";
                message = "Јесте ли сигурни да желите додати приход на активност " + activity.getName() + "?";

            }
            boolean result = FormLoader.showConfirmationDialog(title, message);
            if (result == true) {
                try {
                    result = user.getProjectHandler().addIncome(project, income);
                    if (result == true) {
                        listFinances.getItems().add(new FinanceWrapper(income, type));
                        project.getIncomeList().add(income);
                    }
                } catch (StoringException ex) {
                    FormLoader.showErrorDialog("Грешка", "Грешка приликом уписа у базу.");
                    ((Stage)btnAdd.getScene().getWindow()).close();
                }
            }
        } else {
            Expense exp = new Expense();
            exp.setMoneyAmmount(BigDecimal.valueOf(Double.parseDouble(tfAmount.getText())));
            exp.setCurrency(cbCurrency.getValue());
            exp.setDescription(taDescription.getText());
            exp.setIDProject(project);
            String title = "Упозорење";
            String message = "Јесте ли сигурни да желите додати трошак на пројекат ?";
            if (activity != null) {
                exp.setIDActivity(activity);
                title = "Упозорење";
                message = "Јесте ли сигурни да желите додати трошак на активност " + activity.getName() + "?";
            }
            boolean result = FormLoader.showConfirmationDialog(title, message);
            if (result == true) {
                try {
                    result = user.getProjectHandler().addExpense(project, exp);
                    if (result == true) {
                        System.out.println("Tu sam, kod dodavanja liste");
                        listFinances.getItems().add(new FinanceWrapper(exp, type));
                        project.getExpenseList().add(exp);
                    }
                } catch (StoringException ex) {
                    FormLoader.showErrorDialog("Грешка", "Грешка приликом уписа у базу.");
                    ((Stage)btnAdd.getScene().getWindow()).close();
                }
            }
        }
    }

    /**
     * Ne mogu da skontam bolji nacin da odradim ovo
     */
    private static class FinanceWrapper {

        Object finance;
        boolean type;

        public FinanceWrapper(Object finance, boolean type) {
            this.finance = finance;
            this.type = type;
        }

        public Object getFinance() {
            return finance;
        }

        public void setFinance(Object finance) {
            this.finance = finance;
        }

        public boolean isType() {
            return type;
        }

        public void setType(boolean type) {
            this.type = type;
        }

        @Override
        public String toString() {
            String name = "";
            if (type == true) {
                Income income = (Income) finance;
                name = "Износ: " + String.format("%.02f",income.getMoneyAmmount().doubleValue()) + " " + income.getCurrency();
            } else {
                Expense expense = (Expense) finance;
                name = "Износ: " + String.format("%.02f",expense.getMoneyAmmount()) + " " + expense.getCurrency();
            }
            return name;
        }
    }

}
