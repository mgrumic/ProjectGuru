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
public class ClosureTasksPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDParent")
    private int iDParent;
    @Basic(optional = false)
    @Column(name = "IDChild")
    private int iDChild;

    public ClosureTasksPK() {
    }

    public ClosureTasksPK(int iDParent, int iDChild) {
        this.iDParent = iDParent;
        this.iDChild = iDChild;
    }

    public int getIDParent() {
        return iDParent;
    }

    public void setIDParent(int iDParent) {
        this.iDParent = iDParent;
    }

    public int getIDChild() {
        return iDChild;
    }

    public void setIDChild(int iDChild) {
        this.iDChild = iDChild;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) iDParent;
        hash += (int) iDChild;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClosureTasksPK)) {
            return false;
        }
        ClosureTasksPK other = (ClosureTasksPK) object;
        if (this.iDParent != other.iDParent) {
            return false;
        }
        if (this.iDChild != other.iDChild) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.ClosureTasksPK[ iDParent=" + iDParent + ", iDChild=" + iDChild + " ]";
    }
    
}
