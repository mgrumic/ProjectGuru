/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import projectguru.controllers.util.SerbianLocalDateStringConverter;

/**
 *
 * @author medlan
 */
public class ProjectReportBean {
    private int percentage;
    private String taskName;
    private int assumedManHours;
    private double doneManHours;
    private String startDate;
    private String endDate;
    private String deаdlineDate;
    
    public double getDoneManHours() {
        return doneManHours;
    }

    public void setDoneManHours(double doneManHours) {
        this.doneManHours = doneManHours;
    }
    

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDeаdlineDate() {
        return deаdlineDate;
    }

    public void setDeаdlineDate(String dedlineDate) {
        this.deаdlineDate = dedlineDate;
    }
    
    public int getAssumedManHours() {
        return assumedManHours;
    }

    public void setAssumedManHours(int assumedManHours) {
        this.assumedManHours = assumedManHours;
    }
    
    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public ProjectReportBean(String taskName, int percentage, int assumedManHours,
                             Date startDate, Date endDate, Date deаdlineDate, double doneManHours){
        SerbianLocalDateStringConverter conv = new SerbianLocalDateStringConverter();
        this.startDate       = (startDate == null)?"":conv.toString(ZonedDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault()).toLocalDate());
        this.endDate         = (endDate == null)?"":conv.toString(ZonedDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault()).toLocalDate());
        this.deаdlineDate     = (deаdlineDate == null)?"":conv.toString(ZonedDateTime.ofInstant(deаdlineDate.toInstant(), ZoneId.systemDefault()).toLocalDate());
        this.taskName        = taskName;
        this.percentage      = percentage;
        this.assumedManHours = assumedManHours;
        this.doneManHours = doneManHours;
    }
            
    
}
