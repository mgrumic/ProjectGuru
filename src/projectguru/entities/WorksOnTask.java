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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author ZM
 */
@Entity
@Table(name = "works_on_task")
@NamedQueries({
    @NamedQuery(name = "WorksOnTask.findAll", query = "SELECT w FROM WorksOnTask w"),
    @NamedQuery(name = "WorksOnTask.findByIDTask", query = "SELECT w FROM WorksOnTask w WHERE w.worksOnTaskPK.iDTask = :iDTask"),
    @NamedQuery(name = "WorksOnTask.findByPrivileges", query = "SELECT w FROM WorksOnTask w WHERE w.privileges = :privileges"),
    @NamedQuery(name = "WorksOnTask.findByUsername", query = "SELECT w FROM WorksOnTask w WHERE w.worksOnTaskPK.username = :username"),
    @NamedQuery(name = "WorksOnTask.findByIDProject", query = "SELECT w FROM WorksOnTask w WHERE w.worksOnTaskPK.iDProject = :iDProject"),
    @NamedQuery(name = "WorksOnTask.findByWorking", query = "SELECT w FROM WorksOnTask w WHERE w.working = :working")})
public class WorksOnTask implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorksOnTaskPK worksOnTaskPK;
    @Basic(optional = false)
    @Column(name = "Privileges")
    private int privileges;
    @Basic(optional = false)
    @Column(name = "Working")
    private boolean working;
    @OneToMany(mappedBy = "worksOnTask", fetch = FetchType.LAZY)
    private List<Activity> activityList;
    @JoinColumn(name = "IDTask", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Task task;
    @JoinColumns({
        @JoinColumn(name = "Username", referencedColumnName = "Username", insertable = false, updatable = false),
        @JoinColumn(name = "IDProject", referencedColumnName = "IDProject", insertable = false, updatable = false)})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private WorksOnProject worksOnProject;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "worksOnTask", fetch = FetchType.LAZY)
    private Timetable timetable;

    public WorksOnTask() {
    }

    public WorksOnTask(WorksOnTaskPK worksOnTaskPK) {
        this.worksOnTaskPK = worksOnTaskPK;
    }

    public WorksOnTask(WorksOnTaskPK worksOnTaskPK, int privileges, boolean working) {
        this.worksOnTaskPK = worksOnTaskPK;
        this.privileges = privileges;
        this.working = working;
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

    public Timetable getTimetable() {
        return timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
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
