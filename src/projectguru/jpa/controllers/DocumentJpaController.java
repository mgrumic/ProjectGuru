/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectguru.jpa.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import projectguru.entities.Project;
import projectguru.entities.DocumentRevision;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.Document;
import projectguru.jpa.controllers.exceptions.IllegalOrphanException;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author marko
 */
public class DocumentJpaController implements Serializable {

    public DocumentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Document document) {
        if (document.getDocumentRevisionList() == null) {
            document.setDocumentRevisionList(new ArrayList<DocumentRevision>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project IDProject = document.getIDProject();
            if (IDProject != null) {
                IDProject = em.getReference(IDProject.getClass(), IDProject.getId());
                document.setIDProject(IDProject);
            }
            List<DocumentRevision> attachedDocumentRevisionList = new ArrayList<DocumentRevision>();
            for (DocumentRevision documentRevisionListDocumentRevisionToAttach : document.getDocumentRevisionList()) {
                documentRevisionListDocumentRevisionToAttach = em.getReference(documentRevisionListDocumentRevisionToAttach.getClass(), documentRevisionListDocumentRevisionToAttach.getId());
                attachedDocumentRevisionList.add(documentRevisionListDocumentRevisionToAttach);
            }
            document.setDocumentRevisionList(attachedDocumentRevisionList);
            em.persist(document);
            if (IDProject != null) {
                IDProject.getDocumentList().add(document);
                IDProject = em.merge(IDProject);
            }
            for (DocumentRevision documentRevisionListDocumentRevision : document.getDocumentRevisionList()) {
                Document oldIDDocumentOfDocumentRevisionListDocumentRevision = documentRevisionListDocumentRevision.getIDDocument();
                documentRevisionListDocumentRevision.setIDDocument(document);
                documentRevisionListDocumentRevision = em.merge(documentRevisionListDocumentRevision);
                if (oldIDDocumentOfDocumentRevisionListDocumentRevision != null) {
                    oldIDDocumentOfDocumentRevisionListDocumentRevision.getDocumentRevisionList().remove(documentRevisionListDocumentRevision);
                    oldIDDocumentOfDocumentRevisionListDocumentRevision = em.merge(oldIDDocumentOfDocumentRevisionListDocumentRevision);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Document document) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Document persistentDocument = em.find(Document.class, document.getId());
            Project IDProjectOld = persistentDocument.getIDProject();
            Project IDProjectNew = document.getIDProject();
            List<DocumentRevision> documentRevisionListOld = persistentDocument.getDocumentRevisionList();
            List<DocumentRevision> documentRevisionListNew = document.getDocumentRevisionList();
            List<String> illegalOrphanMessages = null;
            for (DocumentRevision documentRevisionListOldDocumentRevision : documentRevisionListOld) {
                if (!documentRevisionListNew.contains(documentRevisionListOldDocumentRevision)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentRevision " + documentRevisionListOldDocumentRevision + " since its IDDocument field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (IDProjectNew != null) {
                IDProjectNew = em.getReference(IDProjectNew.getClass(), IDProjectNew.getId());
                document.setIDProject(IDProjectNew);
            }
            List<DocumentRevision> attachedDocumentRevisionListNew = new ArrayList<DocumentRevision>();
            for (DocumentRevision documentRevisionListNewDocumentRevisionToAttach : documentRevisionListNew) {
                documentRevisionListNewDocumentRevisionToAttach = em.getReference(documentRevisionListNewDocumentRevisionToAttach.getClass(), documentRevisionListNewDocumentRevisionToAttach.getId());
                attachedDocumentRevisionListNew.add(documentRevisionListNewDocumentRevisionToAttach);
            }
            documentRevisionListNew = attachedDocumentRevisionListNew;
            document.setDocumentRevisionList(documentRevisionListNew);
            document = em.merge(document);
            if (IDProjectOld != null && !IDProjectOld.equals(IDProjectNew)) {
                IDProjectOld.getDocumentList().remove(document);
                IDProjectOld = em.merge(IDProjectOld);
            }
            if (IDProjectNew != null && !IDProjectNew.equals(IDProjectOld)) {
                IDProjectNew.getDocumentList().add(document);
                IDProjectNew = em.merge(IDProjectNew);
            }
            for (DocumentRevision documentRevisionListNewDocumentRevision : documentRevisionListNew) {
                if (!documentRevisionListOld.contains(documentRevisionListNewDocumentRevision)) {
                    Document oldIDDocumentOfDocumentRevisionListNewDocumentRevision = documentRevisionListNewDocumentRevision.getIDDocument();
                    documentRevisionListNewDocumentRevision.setIDDocument(document);
                    documentRevisionListNewDocumentRevision = em.merge(documentRevisionListNewDocumentRevision);
                    if (oldIDDocumentOfDocumentRevisionListNewDocumentRevision != null && !oldIDDocumentOfDocumentRevisionListNewDocumentRevision.equals(document)) {
                        oldIDDocumentOfDocumentRevisionListNewDocumentRevision.getDocumentRevisionList().remove(documentRevisionListNewDocumentRevision);
                        oldIDDocumentOfDocumentRevisionListNewDocumentRevision = em.merge(oldIDDocumentOfDocumentRevisionListNewDocumentRevision);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = document.getId();
                if (findDocument(id) == null) {
                    throw new NonexistentEntityException("The document with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Document document;
            try {
                document = em.getReference(Document.class, id);
                document.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The document with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DocumentRevision> documentRevisionListOrphanCheck = document.getDocumentRevisionList();
            for (DocumentRevision documentRevisionListOrphanCheckDocumentRevision : documentRevisionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Document (" + document + ") cannot be destroyed since the DocumentRevision " + documentRevisionListOrphanCheckDocumentRevision + " in its documentRevisionList field has a non-nullable IDDocument field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Project IDProject = document.getIDProject();
            if (IDProject != null) {
                IDProject.getDocumentList().remove(document);
                IDProject = em.merge(IDProject);
            }
            em.remove(document);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Document> findDocumentEntities() {
        return findDocumentEntities(true, -1, -1);
    }

    public List<Document> findDocumentEntities(int maxResults, int firstResult) {
        return findDocumentEntities(false, maxResults, firstResult);
    }

    private List<Document> findDocumentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Document.class));
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

    public Document findDocument(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Document.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Document> rt = cq.from(Document.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
