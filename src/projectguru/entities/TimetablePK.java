/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectguru.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author marko
 */
@Embeddable
public class TimetablePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "StartTime")
    @Temporal(TemporalType.DATE)
    private Date startTime;
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

    public TimetablePK(Date startTime, int iDTask, String username, int iDProject) {
        this.startTime = startTime;
        this.iDTask = iDTask;
        this.username = username;
        this.iDProject = iDProject;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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
        hash += (startTime != null ? startTime.hashCode() : 0);
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
        if ((this.startTime == null && other.startTime != null) || (this.startTime != null && !this.startTime.equals(other.startTime))) {
            return false;
        }
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
        return "projectguru.entities.TimetablePK[ startTime=" + startTime + ", iDTask=" + iDTask + ", username=" + username + ", iDProject=" + iDProject + " ]";
    }
    
}
