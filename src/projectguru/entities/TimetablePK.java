/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author ZM
 */
@Embeddable
public class TimetablePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDTask")
    private int iDTask;
    @Basic(optional = false)
    @Column(name = "Username")
    private String username;
    @Basic(optional = false)
    @Column(name = "IDProject")
    private int iDProject;

    public TimetablePK() {
    }

    public TimetablePK(int iDTask, String username, int iDProject) {
        this.iDTask = iDTask;
        this.username = username;
        this.iDProject = iDProject;
    }

    public int getIDTask() {
        return iDTask;
    }

    public void setIDTask(int iDTask) {
        this.iDTask = iDTask;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIDProject() {
        return iDProject;
    }

    public void setIDProject(int iDProject) {
        this.iDProject = iDProject;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) iDTask;
        hash += (username != null ? username.hashCode() : 0);
        hash += (int) iDProject;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TimetablePK)) {
            return false;
        }
        TimetablePK other = (TimetablePK) object;
        if (this.iDTask != other.iDTask) {
            return false;
        }
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        if (this.iDProject != other.iDProject) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.TimetablePK[ iDTask=" + iDTask + ", username=" + username + ", iDProject=" + iDProject + " ]";
    }
    
}
