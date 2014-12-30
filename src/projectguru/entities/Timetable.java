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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ZM
 */
@Entity
@Table(name = "timetable")
@NamedQueries({
    @NamedQuery(name = "Timetable.findAll", query = "SELECT t FROM Timetable t"),
    @NamedQuery(name = "Timetable.findByStartTime", query = "SELECT t FROM Timetable t WHERE t.startTime = :startTime"),
    @NamedQuery(name = "Timetable.findByEndTime", query = "SELECT t FROM Timetable t WHERE t.endTime = :endTime"),
    @NamedQuery(name = "Timetable.findByIDTask", query = "SELECT t FROM Timetable t WHERE t.timetablePK.iDTask = :iDTask"),
    @NamedQuery(name = "Timetable.findByUsername", query = "SELECT t FROM Timetable t WHERE t.timetablePK.username = :username"),
    @NamedQuery(name = "Timetable.findByIDProject", query = "SELECT t FROM Timetable t WHERE t.timetablePK.iDProject = :iDProject")})
public class Timetable implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TimetablePK timetablePK;
    @Basic(optional = false)
    @Column(name = "StartTime")
    @Temporal(TemporalType.DATE)
    private Date startTime;
    @Basic(optional = false)
    @Column(name = "EndTime")
    @Temporal(TemporalType.DATE)
    private Date endTime;
    @JoinColumns({
        @JoinColumn(name = "IDTask", referencedColumnName = "IDTask", insertable = false, updatable = false),
        @JoinColumn(name = "Username", referencedColumnName = "Username", insertable = false, updatable = false),
        @JoinColumn(name = "IDProject", referencedColumnName = "IDProject", insertable = false, updatable = false)})
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private WorksOnTask worksOnTask;

    public Timetable() {
    }

    public Timetable(TimetablePK timetablePK) {
        this.timetablePK = timetablePK;
    }

    public Timetable(TimetablePK timetablePK, Date startTime, Date endTime) {
        this.timetablePK = timetablePK;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timetable(int iDTask, String username, int iDProject) {
        this.timetablePK = new TimetablePK(iDTask, username, iDProject);
    }

    public TimetablePK getTimetablePK() {
        return timetablePK;
    }

    public void setTimetablePK(TimetablePK timetablePK) {
        this.timetablePK = timetablePK;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public WorksOnTask getWorksOnTask() {
        return worksOnTask;
    }

    public void setWorksOnTask(WorksOnTask worksOnTask) {
        this.worksOnTask = worksOnTask;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (timetablePK != null ? timetablePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Timetable)) {
            return false;
        }
        Timetable other = (Timetable) object;
        if ((this.timetablePK == null && other.timetablePK != null) || (this.timetablePK != null && !this.timetablePK.equals(other.timetablePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.Timetable[ timetablePK=" + timetablePK + " ]";
    }
    
}
