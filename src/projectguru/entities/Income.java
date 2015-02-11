/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "income")
@NamedQueries({
    @NamedQuery(name = "Income.findAll", query = "SELECT i FROM Income i"),
    @NamedQuery(name = "Income.findByDescription", query = "SELECT i FROM Income i WHERE i.description = :description"),
    @NamedQuery(name = "Income.findByMoneyAmmount", query = "SELECT i FROM Income i WHERE i.moneyAmmount = :moneyAmmount"),
    @NamedQuery(name = "Income.findById", query = "SELECT i FROM Income i WHERE i.id = :id"),
    @NamedQuery(name = "Income.findByCurrency", query = "SELECT i FROM Income i WHERE i.currency = :currency")})
public class Income implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "Description")
    private String description;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "MoneyAmmount")
    private BigDecimal moneyAmmount;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Currency")
    private String currency;
    @JoinColumn(name = "IDActivity", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity iDActivity;
    @JoinColumn(name = "IDProject", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project iDProject;

    public Income() {
    }

    public Income(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMoneyAmmount() {
        return moneyAmmount;
    }

    public void setMoneyAmmount(BigDecimal moneyAmmount) {
        this.moneyAmmount = moneyAmmount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Activity getIDActivity() {
        return iDActivity;
    }

    public void setIDActivity(Activity iDActivity) {
        this.iDActivity = iDActivity;
    }

    public Project getIDProject() {
        return iDProject;
    }

    public void setIDProject(Project iDProject) {
        this.iDProject = iDProject;
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
        if (!(object instanceof Income)) {
            return false;
        }
        Income other = (Income) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.Income[ id=" + id + " ]";
    }
    
}
