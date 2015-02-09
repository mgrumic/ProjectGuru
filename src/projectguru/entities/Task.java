/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.entities;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ZM
 */
@Entity
@Table(name = "task")
@NamedQueries({
    @NamedQuery(name = "Task.findAll", query = "SELECT t FROM Task t"),
    @NamedQuery(name = "Task.findById", query = "SELECT t FROM Task t WHERE t.id = :id"),
    @NamedQuery(name = "Task.findByName", query = "SELECT t FROM Task t WHERE t.name = :name"),
    @NamedQuery(name = "Task.findByDescription", query = "SELECT t FROM Task t WHERE t.description = :description"),
    @NamedQuery(name = "Task.findByAssumedManHours", query = "SELECT t FROM Task t WHERE t.assumedManHours = :assumedManHours"),
    @NamedQuery(name = "Task.findByStartDate", query = "SELECT t FROM Task t WHERE t.startDate = :startDate"),
    @NamedQuery(name = "Task.findByEndDate", query = "SELECT t FROM Task t WHERE t.endDate = :endDate"),
    @NamedQuery(name = "Task.findByDeadline", query = "SELECT t FROM Task t WHERE t.deadline = :deadline")})
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @Column(name = "AssumedManHours")
    private int assumedManHours;
    @Basic(optional = true)
    @Column(name = "StartDate")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Basic(optional = true)
    @Column(name = "EndDate")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Basic(optional = false)
    @Column(name = "Deadline")
    @Temporal(TemporalType.DATE)
    private Date deadline;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "task", fetch = FetchType.LAZY)
    private List<WorksOnTask> worksOnTaskList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", fetch = FetchType.LAZY)
    private List<ClosureTasks> closureTasksChildren;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "child", fetch = FetchType.LAZY)
    private List<ClosureTasks> closureTasksParents;
    @OneToMany(mappedBy = "iDRootTask", fetch = FetchType.LAZY)
    private List<Project> projectList;

    public Task() {
        //worksOnTaskList = new ArrayList<>();
        //closureTasksChildren = new ArrayList<>();
        //closureTasksParents = new ArrayList<>();
        //projectList = new ArrayList<>();
    }

    public Task(Integer id) {
        this();
        this.id = id;
    }

    public Task(Integer id, String name, int assumedManHours, Date startDate, Date endDate, Date deadline) {
        this();
        this.id = id;
        this.name = name;
        this.assumedManHours = assumedManHours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deadline = deadline;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAssumedManHours() {
        return assumedManHours;
    }

    public void setAssumedManHours(int assumedManHours) {
        this.assumedManHours = assumedManHours;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<WorksOnTask> getWorksOnTaskList() {
        return worksOnTaskList;
    }
    
        public List<WorksOnTask> getWorksOnTasksListNonRemoved() {
        return new ArrayList<>(worksOnTaskList)
                        .stream()
                        .filter((wot) -> !wot.getRemoved())
                        .collect(Collectors.toList());
    }

    public void setWorksOnTaskList(List<WorksOnTask> worksOnTaskList) {
        this.worksOnTaskList = worksOnTaskList;
    }

    public List<ClosureTasks> getClosureTasksChildren() {
        return closureTasksChildren;
    }

    public void setClosureTasksChildren(List<ClosureTasks> closureTasksChildren) {
        this.closureTasksChildren = closureTasksChildren;
    }

    public List<ClosureTasks> getClosureTasksParents() {
        return  closureTasksParents;
    }

    public void setClosureTasksParents(List<ClosureTasks> closureTasksParents) {
        this.closureTasksParents = closureTasksParents;
    }

    public List<Project> getProjectList() {
        return projectList;
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Task)) {
            return false;
        }
        Task other = (Task) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.Task[ id=" + id + " ]";
    }
    
}
