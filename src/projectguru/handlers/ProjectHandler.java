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
import projectguru.entities.Project;
import projectguru.entities.User;

/**
 *
 * @author ZM
 */
public interface ProjectHandler {

    public boolean checkProjectChefPrivileges(Project project);

    public boolean checkMemberPrivileges(Project project);

    public boolean checkInsightPrivileges(Project project);

    public boolean createProject(Project project);

    public boolean editProject(Project project);

    public boolean deleteProject(Project project);

    public boolean setChef(Project project, User user);

    public boolean addMember(Project project, User user);

    public boolean addDocument(Project project, Document document);

    public List<Document> getAllDocuments(Project project);

    public boolean addExpense(Project project, Expense exp);

    public boolean addIncome(Project project, Income inc);
}
