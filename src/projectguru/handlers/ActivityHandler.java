/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import projectguru.entities.Activity;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.entities.Task;

/**
 *
 * @author ZM
 */
public interface ActivityHandler {
    public boolean addActivity(Task task, Activity activity);

    public boolean editActivity(Activity activity);

    public boolean deleteActivity(Activity activity);

    public boolean addExpense(Activity activity, Expense exp);

    public boolean addIncome(Activity activity, Income inc);
}
