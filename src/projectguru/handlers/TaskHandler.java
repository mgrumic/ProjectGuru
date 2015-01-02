/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import projectguru.entities.Activity;
import projectguru.entities.Task;
import projectguru.entities.Timetable;
import projectguru.entities.User;

/**
 *
 * @author ZM
 */
public interface TaskHandler {
    
    public boolean checkTaskChefPrivileges(Task task);
    public boolean checkMemberPrivileges(Task task);
    public boolean checkInsightPrivileges(Task task);
    
    public boolean addSubtask(Task task, Task subtask);
    public boolean editSubtask(Task task);
    public boolean deleteSubtask(Task task);
    
    public boolean setChef(Task task, User user);
    public User getChef(Task task);
    public boolean addMember(Task task, User user);
    
    public boolean addActivity(Task task, Activity activity);
    
    public boolean addTimetable(Timetable timetable);
    public boolean editTimetable(Timetable timetable);
    public Double getWorkedManHoursOfTaskSubtree(Task task);
    
    public boolean isMember(Task task, User user);
    public Task getActiveTask();
    
    
}
