/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import projectguru.entities.User;

/**
 *
 * @author ZM
 */
//TODO: razmisliti da ovo bude apstaktna klasa koja ce u sebi sadrzavati User objekat.
// Ja sav to vec uradio, ali prepravite ako mislite da nije dobro iz nekog razloga.
//Ili meni recite pa cu ja prepraviti. Marko Ivanovic.
public abstract class LoggedUser {
    
    
    protected boolean loggedIn;
    protected User user;
    
    public abstract ProjectHandler getProjectHandler();
    public abstract TaskHandler getTaskHandler();
    public abstract UserHandler getUserHandler();
    public abstract ActivityHandler getActivityHandler();
    public abstract DocumentHandler getDocumentHandler();
    public abstract boolean startWorkSession();
    public abstract boolean endWorkSession();
    public abstract ReportHandler getReportHandler();
    
     public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    
}
