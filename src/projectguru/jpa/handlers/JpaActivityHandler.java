/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import projectguru.AccessManager;
import projectguru.entities.Activity;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.entities.Task;
import projectguru.handlers.ActivityHandler;
import projectguru.handlers.LoggedUser;
import projectguru.jpa.JpaAccessManager;
import projectguru.jpa.controllers.ActivityJpaController;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author ZM
 */
public class JpaActivityHandler implements ActivityHandler {

    private LoggedUser user;
    
    public JpaActivityHandler(LoggedUser user)
    {
        this.user = user;
    }
    
    
    //@Override
    //public boolean addActivity(Task task, Activity activity) {
    //    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}

    @Override
    public boolean editActivity(Activity activity) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        ActivityJpaController actCtrl = new ActivityJpaController(emf);
        try {
            actCtrl.edit(activity);
        } catch (Exception ex) {
            Logger.getLogger(JpaActivityHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean deleteActivity(Activity activity) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        ActivityJpaController actCtrl = new ActivityJpaController(emf);
        try {
            actCtrl.destroy(activity.getId());
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(JpaActivityHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean addExpense(Activity activity, Expense exp) {
        List<Expense> expenseList = activity.getExpenseList();
        expenseList.add(exp);
        activity.setExpenseList(expenseList);
        exp.setIDActivity(activity);
        return true;
    }

    @Override
    public boolean addIncome(Activity activity, Income inc) {
        List<Income> incomeList = activity.getIncomeList();
        incomeList.add(inc);
        activity.setIncomeList(incomeList);
        inc.setIDActivity(activity);
        return true;
    }
    
}
