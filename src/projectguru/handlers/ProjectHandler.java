/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import java.util.List;
import projectguru.entities.Document;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.entities.Privileges;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;

/**
 *
 * @author ZM
 */
public interface ProjectHandler {

    public boolean checkProjectChefPrivileges(Project project);

    public boolean checkMemberPrivileges(Project project);

    public boolean checkInsightPrivileges(Project project);

    public List<Project> getAllProjects();
    
    public List<User> getAllMembers(Project project);
    
    public boolean createProject(Project project) throws  InsuficientPrivilegesException, StoringException;
    public boolean editProject(Project project) throws InsuficientPrivilegesException, EntityDoesNotExistException, StoringException;
    public boolean deleteProject(Project project) throws InsuficientPrivilegesException, EntityDoesNotExistException, StoringException;

    public void setRootTask(Project project, Task task)throws InsuficientPrivilegesException, EntityDoesNotExistException, StoringException;
    
    public boolean setPrivileges(Project project, User user, Privileges privileges) throws StoringException;
    public boolean addMember(Project project, User user) throws StoringException;
    public boolean connectionExists(Project project, User user);
    
    public boolean isChef(Project project, User user);
    public boolean isMember(Project project, User user);
    public boolean isInsight(Project project, User user);
    
    public boolean addDocument(Project project, Document document) throws InsuficientPrivilegesException, StoringException ;
    public List<Document> getAllDocuments(Project project) throws EntityDoesNotExistException;

    public boolean addExpense(Project project, Expense exp) throws StoringException;
    public boolean addIncome(Project project, Income inc) throws StoringException;
    
    public Project getUpdatedProject(Project project) throws EntityDoesNotExistException;
    public User getUpdatedUser(User user) throws EntityDoesNotExistException;
       
}
