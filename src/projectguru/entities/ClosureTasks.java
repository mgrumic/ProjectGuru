/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author ZM
 */
@Entity
@Table(name = "closure_tasks")
@NamedQueries({
    @NamedQuery(name = "ClosureTasks.findAll", query = "SELECT c FROM ClosureTasks c"),
    @NamedQuery(name = "ClosureTasks.findByIDParent", query = "SELECT c FROM ClosureTasks c WHERE c.closureTasksPK.iDParent = :iDParent"),
    @NamedQuery(name = "ClosureTasks.findByDepth", query = "SELECT c FROM ClosureTasks c WHERE c.depth = :depth"),
    @NamedQuery(name = "ClosureTasks.findByIDChild", query = "SELECT c FROM ClosureTasks c WHERE c.closureTasksPK.iDChild = :iDChild")})
public class ClosureTasks implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ClosureTasksPK closureTasksPK;
    @Basic(optional = false)
    @Column(name = "Depth")
    private int depth;
    @JoinColumn(name = "IDParent", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Task task;
    @JoinColumn(name = "IDChild", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Task task1;

    public ClosureTasks() {
    }

    public ClosureTasks(ClosureTasksPK closureTasksPK) {
        this.closureTasksPK = closureTasksPK;
    }

    public ClosureTasks(ClosureTasksPK closureTasksPK, int depth) {
        this.closureTasksPK = closureTasksPK;
        this.depth = depth;
    }

    public ClosureTasks(int iDParent, int iDChild) {
        this.closureTasksPK = new ClosureTasksPK(iDParent, iDChild);
    }

    public ClosureTasksPK getClosureTasksPK() {
        return closureTasksPK;
    }

    public void setClosureTasksPK(ClosureTasksPK closureTasksPK) {
        this.closureTasksPK = closureTasksPK;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask1() {
        return task1;
    }

    public void setTask1(Task task1) {
        this.task1 = task1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (closureTasksPK != null ? closureTasksPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClosureTasks)) {
            return false;
        }
        ClosureTasks other = (ClosureTasks) object;
        if ((this.closureTasksPK == null && other.closureTasksPK != null) || (this.closureTasksPK != null && !this.closureTasksPK.equals(other.closureTasksPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.ClosureTasks[ closureTasksPK=" + closureTasksPK + " ]";
    }
    
}
