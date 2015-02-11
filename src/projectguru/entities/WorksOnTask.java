/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author marko
 */
@Entity
@Table(name = "works_on_task")
@NamedQueries({
    @NamedQuery(name = "WorksOnTask.findAll", query = "SELECT w FROM WorksOnTask w"),
    @NamedQuery(name = "WorksOnTask.findByIDTask", query = "SELECT w FROM WorksOnTask w WHERE w.worksOnTaskPK.iDTask = :iDTask"),
    @NamedQuery(name = "WorksOnTask.findByPrivileges", query = "SELECT w FROM WorksOnTask w WHERE w.privileges = :privileges"),
    @NamedQuery(name = "WorksOnTask.findByUsername", query = "SELECT w FROM WorksOnTask w WHERE w.worksOnTaskPK.username = :username"),
    @NamedQuery(name = "WorksOnTask.findByIDProject", query = "SELECT w FROM WorksOnTask w WHERE w.worksOnTaskPK.iDProject = :iDProject"),
    @NamedQuery(name = "WorksOnTask.findByWorking", query = "SELECT w FROM WorksOnTask w WHERE w.working = :working"),
    @NamedQuery(name = "WorksOnTask.findByRemoved", query = "SELECT w FROM WorksOnTask w WHERE w.removed = :removed")})
public class WorksOnTask implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorksOnTaskPK worksOnTaskPK;
    @Basic(optional = false)
    @Column(nullable = false)
    private int privileges;
    @Basic(optional = false)
    @Column(nullable = false)
    private boolean working;
    @Basic(optional = false)
    @Column(nullable = false)
    private boolean removed;
    @OneToMany(mappedBy = "worksOnTask", fetch = FetchType.LAZY)
    private List<Activity> activityList;
    @JoinColumn(name = "IDTask", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Task task;
    @JoinColumn(name = "Username", referencedColumnName = "Username", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @JoinColumns({
        @JoinColumn(name = "Username", referencedColumnName = "Username", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "IDProject", referencedColumnName = "IDProject", nullable = false, insertable = false, updatable = false)})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private WorksOnProject worksOnProject;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "worksOnTask", fetch = FetchType.LAZY)
    private List<Timetable> timetableList;

    public WorksOnTask() {
    }

    public WorksOnTask(WorksOnTaskPK worksOnTaskPK) {
        this.worksOnTaskPK = worksOnTaskPK;
    }

    public WorksOnTask(WorksOnTaskPK worksOnTaskPK, int privileges, boolean working, boolean removed) {
        this.worksOnTaskPK = worksOnTaskPK;
        this.privileges = privileges;
        this.working = working;
        this.removed = removed;
    }

    public WorksOnTask(int iDTask, String username, int iDProject) {
        this.worksOnTaskPK = new WorksOnTaskPK(iDTask, username, iDProject);
    }

    public WorksOnTaskPK getWorksOnTaskPK() {
        return worksOnTaskPK;
    }

    public void setWorksOnTaskPK(WorksOnTaskPK worksOnTaskPK) {
        this.worksOnTaskPK = worksOnTaskPK;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public int getPrivileges() {
        return privileges;
    }

    public void setPrivileges(int privileges) {
        this.privileges = privileges;
    }

    public boolean getWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public boolean getRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public WorksOnProject getWorksOnProject() {
        return worksOnProject;
    }

    public void setWorksOnProject(WorksOnProject worksOnProject) {
        this.worksOnProject = worksOnProject;
    }

    public List<Timetable> getTimetableList() {
        return timetableList;
    }

    public void setTimetableList(List<Timetable> timetableList) {
        this.timetableList = timetableList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (worksOnTaskPK != null ? worksOnTaskPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorksOnTask)) {
            return false;
        }
        WorksOnTask other = (WorksOnTask) object;
        if ((this.worksOnTaskPK == null && other.worksOnTaskPK != null) || (this.worksOnTaskPK != null && !this.worksOnTaskPK.equals(other.worksOnTaskPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.WorksOnTask[ worksOnTaskPK=" + worksOnTaskPK + " ]";
    }
    
}
