/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import java.util.Date;
import java.util.List;
import projectguru.entities.Activity;
import projectguru.entities.Task;
import projectguru.entities.Timetable;
import projectguru.entities.User;
import projectguru.handlers.exceptions.BusyWorkersException;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.handlers.exceptions.UnfinishedSubtaskException;
import projectguru.handlers.exceptions.UserNotTaskMemberException;
import projectguru.tasktree.TaskNode;
import projectguru.tasktree.TaskTree;

/**
 *
 * @author ZM
 */
public interface TaskHandler {
    /**
     * 
     * @param task 
     * @return True if currently logged in user is chef of this task or some of it's super tasks. False otherwise.
     */
    public boolean checkTaskChefPrivileges(Task task);
    public boolean checkMemberPrivileges(Task task);
    public boolean checkInsightPrivileges(Task task);
    
    /**
     *  Adds subtask as child-task to task. 
     *  Task must exist in database or EntityDoesNotExistException will be thrown.
     *  Subtask should not exists in database, it will be added through this operation.
     * 
     * @param task parent task
     * @param subtask subtask - child task
     * @return True if successful, false otherwise
     * @throws EntityDoesNotExistException
     * @throws StoringException
     * @throws InsuficientPrivilegesException 
     */
    public boolean addSubtask(Task task, Task subtask) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException;
    public boolean editSubtask(Task task) throws EntityDoesNotExistException, StoringException;
    public boolean deleteSubtask(Task task) throws EntityDoesNotExistException;
    
    public boolean setChef(Task task, User user) throws EntityDoesNotExistException, StoringException;
    public User getChef(Task task);
    public boolean addMember(Task task, User user) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException;
    
    public boolean addActivity(Task task, Activity activity) throws EntityDoesNotExistException, StoringException;
    
    public boolean addTimetable(Timetable timetable) throws StoringException;
    public boolean editTimetable(Timetable timetable) throws StoringException, EntityDoesNotExistException;
    
    /**
     * 
     * @param task - task for which worked man*hours should be calculated
     * @return Total number of hours worked by member of this task on this task, and summed together. 
     * @throws EntityDoesNotExistException 
     */
    public Double getWorkedManHoursOfTaskSubtree(Task task) throws EntityDoesNotExistException;
    
    public boolean isMember(Task task, User user);
    /**
     * 
     * @return Task that currently logged in user works on.
     */
    public Task getActiveTask();
    public Task getActiveTask(User user) throws  EntityDoesNotExistException;
    public Task getActiveTask(String username) throws  EntityDoesNotExistException;
    
    /**
     * 
     * @param task root of task tree
     * @return TaskTree which has provided task as root
     */
    public TaskTree getTaskTree(Task task);
    /**
     * 
     * @param task
     * @return List of direct children of provided task
     */
    public List<TaskNode> getTaskNodeChildren(Task task);
    
    /**
     * 
     * Same as startTask(task, OnBusyWorkers.SIMPLE)
     * 
     * @param task task to be started
     * @return true if successful, false otherwise.
     * @throws EntityDoesNotExistException
     * @throws BusyWorkersException
     * @throws StoringException 
     */
    public boolean startTask(Task task) throws EntityDoesNotExistException, StoringException;
    /**
     *  Sets task's start date to current date.
     *  Sets this task as active task to all available members.
     *  Member is available if it does not have active task.
     *  If onBusyWorkers is set to TRANSFER_FROM_PARENT_TASK it will set this task as active task to all members
     *  who's active task is direct parent of this task.
     *  If there are unavailable members and onBusyWorkers is THROW_EXCEPTION it will throw BusyWorkersException.
     * 
     * @param task
     * @param onBusyWorkers defines behavior if there are workers active on other tasks, excluding direct parent of this task.
     * @return true if successful.
     * @throws EntityDoesNotExistException
     * @throws BusyWorkersException
     * @throws StoringException 
     */
    public boolean startTask(Task task, OnBusyWorkers onBusyWorkers) throws EntityDoesNotExistException, BusyWorkersException, StoringException;
    
    public boolean endTask(Task task, boolean endSubtasks) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException,UnfinishedSubtaskException;
    public boolean endTask(Task task) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException, UnfinishedSubtaskException;
    
    public boolean setActiveTask(Task task) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException, UserNotTaskMemberException;
    public boolean setActiveTask(Task task, User user) throws EntityDoesNotExistException, StoringException, InsuficientPrivilegesException, UserNotTaskMemberException;
    
    /**
     * Starts new working session (creates new start working time - end working time pair),
     * for currently logged in user working on it's active task.
     * 
     * @param startTime
     * @param endTime
     * @return
     * @throws StoringException 
     */
    public boolean createNewTimetableEntry(Date startTime, Date endTime) throws StoringException;
    
    /**
     * Changes value of end time for last created time table entry.
     * @param endTime
     * @return
     * @throws StoringException 
     */
    public boolean updateActiveTime(Date endTime) throws StoringException;
    
    /**
     * 
     * @param task
     * @return fresh, updated copy of task from database.
     */
    public Task getUpdatedTask(Task task);
    
    
    public enum OnBusyWorkers {
        THROW_EXCEPTION, TRANSFER_FROM_PARENT_TASK, SIMPLE
    }
    
}
