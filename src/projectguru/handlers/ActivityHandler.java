/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import projectguru.entities.Activity;
import projectguru.entities.Expense;
import projectguru.entities.Income;
//import projectguru.entities.Task;

/**
 *
 * @author ZM
 */
public interface ActivityHandler {
    
    //TODO: predlazem konvenciju po kojoj ovakve metode ima onaj hendler koji
    //predstavlja "jedan" stranu u relaciji "jedan-prema-vise". To je u ovom
    //slucaju Task. Ako je "vise-prema-vise" onda nek ove strane imaju ovakve
    //metode. Ispravite me ako grijesim. Marko Ivanovic.
    //P.S. treba razmisliti i o delte. A i o edit, jer treba onemoguciti da
    //se preko edita promjeni povezani Task svojevoljno.
    
    //public boolean addActivity(Task task, Activity activity);

    public boolean editActivity(Activity activity);

    public boolean deleteActivity(Activity activity);

    public boolean addExpense(Activity activity, Expense exp);

    public boolean addIncome(Activity activity, Income inc);
}
