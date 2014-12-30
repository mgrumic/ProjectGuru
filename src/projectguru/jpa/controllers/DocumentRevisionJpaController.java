/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.controllers;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import projectguru.entities.Document;
import projectguru.entities.DocumentRevision;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author ZM
 */
public class DocumentRevisionJpaController implements Serializable {

    public DocumentRevisionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DocumentRevision documentRevision) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Document IDDocument = documentRevision.getIDDocument();
            if (IDDocument != null) {
                IDDocument = em.getReference(IDDocument.getClass(), IDDocument.getId());
                documentRevision.setIDDocument(IDDocument);
            }
            em.persist(documentRevision);
            if (IDDocument != null) {
                IDDocument.getDocumentRevisionList().add(documentRevision);
                IDDocument = em.merge(IDDocument);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DocumentRevision documentRevision) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocumentRevision persistentDocumentRevision = em.find(DocumentRevision.class, documentRevision.getId());
            Document IDDocumentOld = persistentDocumentRevision.getIDDocument();
            Document IDDocumentNew = documentRevision.getIDDocument();
            if (IDDocumentNew != null) {
                IDDocumentNew = em.getReference(IDDocumentNew.getClass(), IDDocumentNew.getId());
                documentRevision.setIDDocument(IDDocumentNew);
            }
            documentRevision = em.merge(documentRevision);
            if (IDDocumentOld != null && !IDDocumentOld.equals(IDDocumentNew)) {
                IDDocumentOld.getDocumentRevisionList().remove(documentRevision);
                IDDocumentOld = em.merge(IDDocumentOld);
            }
            if (IDDocumentNew != null && !IDDocumentNew.equals(IDDocumentOld)) {
                IDDocumentNew.getDocumentRevisionList().add(documentRevision);
                IDDocumentNew = em.merge(IDDocumentNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = documentRevision.getId();
                if (findDocumentRevision(id) == null) {
                    throw new NonexistentEntityException("The documentRevision with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocumentRevision documentRevision;
            try {
                documentRevision = em.getReference(DocumentRevision.class, id);
                documentRevision.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The documentRevision with id " + id + " no longer exists.", enfe);
            }
            Document IDDocument = documentRevision.getIDDocument();
            if (IDDocument != null) {
                IDDocument.getDocumentRevisionList().remove(documentRevision);
                IDDocument = em.merge(IDDocument);
            }
            em.remove(documentRevision);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DocumentRevision> findDocumentRevisionEntities() {
        return findDocumentRevisionEntities(true, -1, -1);
    }

    public List<DocumentRevision> findDocumentRevisionEntities(int maxResults, int firstResult) {
        return findDocumentRevisionEntities(false, maxResults, firstResult);
    }

    private List<DocumentRevision> findDocumentRevisionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DocumentRevision.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DocumentRevision findDocumentRevision(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DocumentRevision.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentRevisionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DocumentRevision> rt = cq.from(DocumentRevision.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
