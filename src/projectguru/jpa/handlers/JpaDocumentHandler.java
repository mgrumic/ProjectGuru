/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import netscape.security.Privilege;
import projectguru.AccessManager;
import projectguru.entities.Document;
import projectguru.entities.DocumentRevision;
import projectguru.entities.WorksOnProject;
import projectguru.entities.Privileges;
import projectguru.entities.Project;
import projectguru.entities.User;
import projectguru.handlers.DocumentHandler;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
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

    public JpaDocumentHandler(LoggedUser user) {
        this.user = user;
    }
//TODO

    @Override
    public boolean checkPrivileges(Document document) {

        if (document.getIDProject() == null) {
            throw new Error("NAJBEM SE MAME");
        }

        return user.getProjectHandler().checkMemberPrivileges(document.getIDProject());

    }

    @Override
    public boolean addRevision(Document original, DocumentRevision revision) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (original.getId() == null || (original = em.find(Document.class, original.getId())) == null) {
                throw new EntityDoesNotExistException("Original document does not exist.");
            }
            if (!checkPrivileges(original)) {
                throw new InsuficientPrivilegesException();
            }
            try {
                em.getTransaction().begin();
                if (revision.getId() == null || (revision = em.find(DocumentRevision.class, revision.getId())) == null) {
                    revision.setIDDocument(original);
                    em.persist(revision);
                    em.flush();
                }

                revision.setIDDocument(original);
                original.getDocumentRevisionList().add(revision);

                em.getTransaction().commit();
                em.refresh(original);
                em.refresh(revision);
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
        return true;
    }

    private boolean addRevisionPrivate(Document original, DocumentRevision revision, EntityManager em) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        if (original.getId() == null || (original = em.find(Document.class, original.getId())) == null) {
            throw new EntityDoesNotExistException("Original document does not exist.");
        }
        if (!checkPrivileges(original)) {
            throw new InsuficientPrivilegesException();
        }

        if (revision.getId() == null || (revision = em.find(DocumentRevision.class, revision.getId())) == null) {
            revision.setIDDocument(original);
            em.persist(revision);
            em.flush();
        }

        revision.setIDDocument(original);
        original.getDocumentRevisionList().add(revision);

        em.refresh(original);
        em.refresh(revision);
        return true;
    }

    @Override
    public boolean addDocument(Project project, Document document, DocumentRevision revision) throws InsuficientPrivilegesException, StoringException {

        if (!user.getProjectHandler().checkMemberPrivileges(project)) {
            throw new InsuficientPrivilegesException();
        }
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {

            em.getTransaction().begin();

            if (project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null) {
                throw new EntityDoesNotExistException("Project does not exist.");
            }

            document.setIDProject(project);
            em.persist(document);
            em.flush();
            
            addRevisionPrivate(document, revision, em);
            em.flush();
            
            em.getTransaction().commit();
            em.persist(revision);
            em.refresh(document);
            em.refresh(project);
            
            // System.out.println(document);


        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();

            throw new StoringException(ex.getLocalizedMessage());
        } finally {
            em.close();
        }
        
        return true;
    }

    @Override
    public boolean editDocument(Document document) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (document.getId() == null || em.find(Document.class, document.getId()) == null) {
                throw new EntityDoesNotExistException("Task does not exist in database.");
            }
            if (checkPrivileges(document)) {
                throw new InsuficientPrivilegesException();
            }
            try {
                em.getTransaction().begin();
                em.merge(document);
                em.getTransaction().commit();

            } catch (Exception ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }

        } finally {
            em.close();
        }
        return true;
    }

    @Override
    public boolean editRevision(DocumentRevision revision) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {
            if (revision.getId() == null || em.find(DocumentRevision.class, revision.getId()) == null) {
                throw new EntityDoesNotExistException("Task does not exist in database.");
            }
            if (checkPrivileges(revision.getIDDocument())) {
                throw new InsuficientPrivilegesException();
            }
            try {
                em.getTransaction().begin();
                em.merge(revision);
                em.getTransaction().commit();

            } catch (Exception ex) {
                Logger.getLogger(JpaTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw new StoringException(ex.getLocalizedMessage());
            }

        } finally {
            em.close();
        }

        return true;
    }

    @Override
    public List<DocumentRevision> getRevisions(Document document) throws EntityDoesNotExistException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (document.getId() == null || (document = em.find(Document.class, document.getId())) == null) {
            throw new EntityDoesNotExistException("Document doesn't exist in database !");
        }

        return document.getDocumentRevisionList();
    }

}
