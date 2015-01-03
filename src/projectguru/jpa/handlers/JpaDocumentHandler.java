/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import projectguru.AccessManager;
import projectguru.entities.Document;
import projectguru.entities.DocumentRevision;
import projectguru.handlers.DocumentHandler;
import projectguru.handlers.LoggedUser;
import projectguru.jpa.JpaAccessManager;
import projectguru.jpa.controllers.DocumentJpaController;
import projectguru.jpa.controllers.DocumentRevisionJpaController;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author ZM
 */
public class JpaDocumentHandler implements DocumentHandler {

    private LoggedUser user;
//    TODO : treba doddati metodu za provjeru privilegija,
//    metodama dodati bacanje izuzetaka
    
    public JpaDocumentHandler(LoggedUser user) {
        this.user = user;
    }
    
    @Override
    public boolean addRevision(Document original, DocumentRevision revision) {
        revision.setIDDocument(original);
        List<DocumentRevision> documentRevisionList = original.getDocumentRevisionList();
        documentRevisionList.add(revision);
        original.setDocumentRevisionList(documentRevisionList);
        return true;
    }

    @Override
    public boolean editDocument(Document document) {
        EntityManagerFactory emf;
        emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        DocumentJpaController documentCtrl = new DocumentJpaController(emf);
        try {
            documentCtrl.edit(document);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(JpaDocumentHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JpaDocumentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean editRevision(DocumentRevision revision) {
        EntityManagerFactory emf;
        emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        DocumentRevisionJpaController docRevCtrl = new DocumentRevisionJpaController(emf);
        try {
            docRevCtrl.edit(revision);
        } catch (Exception ex) {
            Logger.getLogger(JpaDocumentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public List<DocumentRevision> getRevisions(Document document) {
        return document.getDocumentRevisionList();
    }
    
}
