/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

/**
 *
 * @author ZM
 */
public interface LoggedUser {
    
    public ProjectHandler getProjectHandler();
    public TaskHandler getTaskHandler();
    public UserHandler getUserHandler();
    public ActivityHandler getActivityHandler();
    public DocumentHandler getDocumentHandler();
    
}
