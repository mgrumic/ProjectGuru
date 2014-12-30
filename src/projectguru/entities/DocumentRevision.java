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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ZM
 */
@Entity
@Table(name = "document_revision")
@NamedQueries({
    @NamedQuery(name = "DocumentRevision.findAll", query = "SELECT d FROM DocumentRevision d"),
    @NamedQuery(name = "DocumentRevision.findById", query = "SELECT d FROM DocumentRevision d WHERE d.id = :id"),
    @NamedQuery(name = "DocumentRevision.findByNumber", query = "SELECT d FROM DocumentRevision d WHERE d.number = :number"),
    @NamedQuery(name = "DocumentRevision.findByDatePosted", query = "SELECT d FROM DocumentRevision d WHERE d.datePosted = :datePosted"),
    @NamedQuery(name = "DocumentRevision.findByDescription", query = "SELECT d FROM DocumentRevision d WHERE d.description = :description")})
public class DocumentRevision implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Number")
    private int number;
    @Basic(optional = false)
    @Lob
    @Column(name = "BinData")
    private byte[] binData;
    @Basic(optional = false)
    @Column(name = "DatePosted")
    @Temporal(TemporalType.DATE)
    private Date datePosted;
    @Column(name = "Description")
    private String description;
    @JoinColumn(name = "IDDocument", referencedColumnName = "ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Document iDDocument;

    public DocumentRevision() {
    }

    public DocumentRevision(Integer id) {
        this.id = id;
    }

    public DocumentRevision(Integer id, int number, byte[] binData, Date datePosted) {
        this.id = id;
        this.number = number;
        this.binData = binData;
        this.datePosted = datePosted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public byte[] getBinData() {
        return binData;
    }

    public void setBinData(byte[] binData) {
        this.binData = binData;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Document getIDDocument() {
        return iDDocument;
    }

    public void setIDDocument(Document iDDocument) {
        this.iDDocument = iDDocument;
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
        if (!(object instanceof DocumentRevision)) {
            return false;
        }
        DocumentRevision other = (DocumentRevision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "projectguru.entities.DocumentRevision[ id=" + id + " ]";
    }
    
}
