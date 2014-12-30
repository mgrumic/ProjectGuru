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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author ZM
 */
@Entity
@Table(name = "works_on_project")
@NamedQueries({
    @NamedQuery(name = "WorksOnProject.findAll", query = "SELECT w FROM WorksOnProject w"),
    @NamedQuery(name = "WorksOnProject.findByUsername", query = "SELECT w FROM WorksOnProject w WHERE w.worksOnProjectPK.username = :username"),
    @NamedQuery(name = "WorksOnProject.findByIDProject", query = "SELECT w FROM WorksOnProject w WHERE w.worksOnProjectPK.iDProject = :iDProject"),
    @NamedQuery(name = "WorksOnProject.findByPrivileges", query = "SELECT w FROM WorksOnProject w WHERE w.privileges = :privileges")})
public class WorksOnProject implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected WorksOnProjectPK worksOnProjectPK;
    @Basic(optional = false)
    @Column(name = "Privileges")
    private int privileges;
    @JoinColumn(name = "Username", referencedColumnName = "Username", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "IDProject", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project project;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "worksOnProject", fetch = FetchType.LAZY)
    private List<WorksOnTask> worksOnTaskList;

    public WorksOnProject() {
    }

    public WorksOnProject(WorksOnProjectPK worksOnProjectPK) {
        this.worksOnProjectPK = worksOnProjectPK;
    }

    public WorksOnProject(WorksOnProjectPK worksOnProjectPK, int privileges) {
        this.worksOnProjectPK = worksOnProjectPK;
        this.privileges = privileges;
    }

    public WorksOnProject(String username, int iDProject) {
        this.worksOnProjectPK = new WorksOnProjectPK(username, iDProject);
    }

    public WorksOnProjectPK getWorksOnProjectPK() {
        return worksOnProjectPK;
    }

    public void setWorksOnProjectPK(WorksOnProjectPK worksOnProjectPK) {
        this.worksOnProjectPK = worksOnProjectPK;
    }

    public int getPrivileges() {
        return privileges;
    }

    public void setPrivileges(int privileges) {
        this.privileges = privileges;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<WorksOnTask> getWorksOnTaskList() {
        return worksOnTaskList;
    }

    public void setWorksOnTaskList(List<WorksOnTask> worksOnTaskList) {
        this.worksOnTaskList = worksOnTaskList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (worksOnProjectPK != null ? worksOnProjectPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorksOnProject)) {
            return false;
        }
        WorksOnProject other = (WorksOnProject) object;
        if ((this.worksOnProjectPK == null && other.worksOnProjectPK != null) || (this.worksOnProjectPK != null && !this.worksOnProjectPK.equals(other.worksOnProjectPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.WorksOnProject[ worksOnProjectPK=" + worksOnProjectPK + " ]";
    }
    
}
