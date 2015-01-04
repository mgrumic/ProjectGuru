/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import java.util.List;
import projectguru.entities.Activity;
import projectguru.entities.Task;
import projectguru.entities.Timetable;
import projectguru.entities.User;
import projectguru.handlers.exceptions.BusyWorkersException;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.tasktree.TaskNode;
import projectguru.tasktree.TaskTree;

/**
 *
 * @author ZM
 */
public interface TaskHandler {
    
    public boolean checkTaskChefPrivileges(Task task);
    public boolean checkMemberPrivileges(Task task);
    public boolean checkInsightPrivileges(Task task);
    
    public boolean addSubtask(Task task, Task subtask) throws EntityDoesNotExistException, StoringException;
    public boolean editSubtask(Task task) throws EntityDoesNotExistException, StoringException;
    public boolean deleteSubtask(Task task) throws EntityDoesNotExistException;
    
    public boolean setChef(Task task, User user) throws EntityDoesNotExistException, StoringException;
    public User getChef(Task task);
    public boolean addMember(Task task, User user) throws EntityDoesNotExistException, StoringException;
    
    public boolean addActivity(Task task, Activity activity) throws EntityDoesNotExistException, StoringException;
    
    public boolean addTimetable(Timetable timetable) throws StoringException;
    public boolean editTimetable(Timetable timetable) throws StoringException, EntityDoesNotExistException;;
    public Double getWorkedManHoursOfTaskSubtree(Task task);
    
    public boolean isMember(Task task, User user);
    public Task getActiveTask();
    public Task getActiveTask(User user) throws  EntityDoesNotExistException;
    public Task getActiveTask(String username) throws  EntityDoesNotExistException;
    
    public TaskTree getTaskTree(Task task);
    public List<TaskNode> getTaskNodeChildren(Task task);
    
    public boolean startTask(Task task) throws EntityDoesNotExistException, BusyWorkersException, StoringException;
    
    public Task getUpdatedTask(Task task);
    
}
