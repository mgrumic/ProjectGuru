/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import projectguru.entities.User;
import projectguru.handlers.ActivityHandler;
import projectguru.handlers.DocumentHandler;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.ReportHandler;
import projectguru.handlers.TaskHandler;
import projectguru.handlers.UserHandler;

/**
 *
 * @author ZM
 */
public class JpaLoggedUser extends LoggedUser {

    protected ProjectHandler projectHandler;
    protected TaskHandler taskHandler;
    protected UserHandler userHandler;
    protected ActivityHandler activityHandler;
    protected DocumentHandler documentHandler;
    protected ReportHandler reportHandler;
    
    public JpaLoggedUser(User user) {
        this.user = user;
        
        projectHandler = new JpaProjectHandler(this);
        taskHandler = new JpaTaskHandler(this);
        userHandler = new JpaUserHandler(this);
        activityHandler = new JpaActivityHandler(this);
        documentHandler = new JpaDocumentHandler(this);
        reportHandler = new JpaReportHandler(this);
        loggedIn = false;
        
    }
    
    public JpaLoggedUser(User user, boolean isLoggedIn){
        this(user);
        this.loggedIn = isLoggedIn;
    }
       

    @Override
    public ProjectHandler getProjectHandler() {
        return projectHandler;
    }

    @Override
    public TaskHandler getTaskHandler() {
        return taskHandler;
    }

    @Override
    public UserHandler getUserHandler() {
        return userHandler;
    }

    @Override
    public ActivityHandler getActivityHandler() {
        return activityHandler;
    }

    @Override
    public ReportHandler getReportHandler() {
        return reportHandler;
    }
    
    @Override
    public DocumentHandler getDocumentHandler() {
        return documentHandler;
    }

    @Override
    public boolean startWorkSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean endWorkSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
