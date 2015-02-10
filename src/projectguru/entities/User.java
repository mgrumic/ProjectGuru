/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import projectguru.AccessManager;
import projectguru.jpa.JpaAccessManager;

/**
 *
 * @author ZM
 */
@Entity
@Table(name = "user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByAppPrivileges", query = "SELECT u FROM User u WHERE u.appPrivileges = :appPrivileges"),
    @NamedQuery(name = "User.findByActivated", query = "SELECT u FROM User u WHERE u.activated = :activated")})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Username")
    private String username;
    @Basic(optional = false)
    @Column(name = "Password")
    private String password;
    @Basic(optional = false)
    @Column(name = "FirstName")
    private String firstName;
    @Basic(optional = false)
    @Column(name = "LastName")
    private String lastName;
    @Basic(optional = false)
    @Column(name = "AppPrivileges")
    private int appPrivileges;
    @Basic(optional = false)
    @Column(name = "Activated")
    private boolean activated;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private List<WorksOnProject> worksOnProjectList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY)
    private List<WorksOnTask> worksOnTaskList;

    
    public List<WorksOnTask> getWorksOnTaskList(){
        return worksOnTaskList;
    }

    public void setWorksOnTaskList(List<WorksOnTask> worksOnTaskList) {
        this.worksOnTaskList = worksOnTaskList;
    }

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String password, String firstName, String lastName, int appPrivileges, boolean activated) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.appPrivileges = appPrivileges;
        this.activated = activated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAppPrivileges() {
        return appPrivileges;
    }

    public void setAppPrivileges(int appPrivileges) {
        this.appPrivileges = appPrivileges;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @XmlTransient
    public List<WorksOnProject> getWorksOnProjectList() {
        return worksOnProjectList;
    }

    public void setWorksOnProjectList(List<WorksOnProject> worksOnProjectList) {
        this.worksOnProjectList = worksOnProjectList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.User[ username=" + username + " ]";
    }


    public List<WorksOnTask> getWorksOnTaskListNonRemoved() {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        
        try{
            
            Query q = em.createQuery("SELECT wot FROM WorksOnTask wot WHERE wot.worksOnTaskPK.username = :username AND wot.removed = false ", WorksOnTask.class);
            q.setParameter("username", this.username);
            return q.getResultList();

        }finally{
            em.close();
        }

    }
  
    
}
