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
import projectguru.entities.Task;
import projectguru.entities.Income;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import projectguru.entities.WorksOnProject;
import projectguru.entities.Document;
import projectguru.entities.Expense;
import projectguru.entities.Project;
import projectguru.jpa.controllers.exceptions.IllegalOrphanException;
import projectguru.jpa.controllers.exceptions.NonexistentEntityException;

/**
 *
 * @author ZM
 */
public class ProjectJpaController implements Serializable {

    public ProjectJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Project project) {
        if (project.getIncomeList() == null) {
            project.setIncomeList(new ArrayList<Income>());
        }
        if (project.getWorksOnProjectList() == null) {
            project.setWorksOnProjectList(new ArrayList<WorksOnProject>());
        }
        if (project.getDocumentList() == null) {
            project.setDocumentList(new ArrayList<Document>());
        }
        if (project.getExpenseList() == null) {
            project.setExpenseList(new ArrayList<Expense>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Task IDRootTask = project.getIDRootTask();
            if (IDRootTask != null) {
                IDRootTask = em.getReference(IDRootTask.getClass(), IDRootTask.getId());
                project.setIDRootTask(IDRootTask);
            }
            List<Income> attachedIncomeList = new ArrayList<Income>();
            for (Income incomeListIncomeToAttach : project.getIncomeList()) {
                incomeListIncomeToAttach = em.getReference(incomeListIncomeToAttach.getClass(), incomeListIncomeToAttach.getId());
                attachedIncomeList.add(incomeListIncomeToAttach);
            }
            project.setIncomeList(attachedIncomeList);
            List<WorksOnProject> attachedWorksOnProjectList = new ArrayList<WorksOnProject>();
            for (WorksOnProject worksOnProjectListWorksOnProjectToAttach : project.getWorksOnProjectList()) {
                worksOnProjectListWorksOnProjectToAttach = em.getReference(worksOnProjectListWorksOnProjectToAttach.getClass(), worksOnProjectListWorksOnProjectToAttach.getWorksOnProjectPK());
                attachedWorksOnProjectList.add(worksOnProjectListWorksOnProjectToAttach);
            }
            project.setWorksOnProjectList(attachedWorksOnProjectList);
            List<Document> attachedDocumentList = new ArrayList<Document>();
            for (Document documentListDocumentToAttach : project.getDocumentList()) {
                documentListDocumentToAttach = em.getReference(documentListDocumentToAttach.getClass(), documentListDocumentToAttach.getId());
                attachedDocumentList.add(documentListDocumentToAttach);
            }
            project.setDocumentList(attachedDocumentList);
            List<Expense> attachedExpenseList = new ArrayList<Expense>();
            for (Expense expenseListExpenseToAttach : project.getExpenseList()) {
                expenseListExpenseToAttach = em.getReference(expenseListExpenseToAttach.getClass(), expenseListExpenseToAttach.getId());
                attachedExpenseList.add(expenseListExpenseToAttach);
            }
            project.setExpenseList(attachedExpenseList);
            em.persist(project);
            if (IDRootTask != null) {
                IDRootTask.getProjectList().add(project);
                IDRootTask = em.merge(IDRootTask);
            }
            for (Income incomeListIncome : project.getIncomeList()) {
                Project oldIDProjectOfIncomeListIncome = incomeListIncome.getIDProject();
                incomeListIncome.setIDProject(project);
                incomeListIncome = em.merge(incomeListIncome);
                if (oldIDProjectOfIncomeListIncome != null) {
                    oldIDProjectOfIncomeListIncome.getIncomeList().remove(incomeListIncome);
                    oldIDProjectOfIncomeListIncome = em.merge(oldIDProjectOfIncomeListIncome);
                }
            }
            for (WorksOnProject worksOnProjectListWorksOnProject : project.getWorksOnProjectList()) {
                Project oldProjectOfWorksOnProjectListWorksOnProject = worksOnProjectListWorksOnProject.getProject();
                worksOnProjectListWorksOnProject.setProject(project);
                worksOnProjectListWorksOnProject = em.merge(worksOnProjectListWorksOnProject);
                if (oldProjectOfWorksOnProjectListWorksOnProject != null) {
                    oldProjectOfWorksOnProjectListWorksOnProject.getWorksOnProjectList().remove(worksOnProjectListWorksOnProject);
                    oldProjectOfWorksOnProjectListWorksOnProject = em.merge(oldProjectOfWorksOnProjectListWorksOnProject);
                }
            }
            for (Document documentListDocument : project.getDocumentList()) {
                Project oldIDProjectOfDocumentListDocument = documentListDocument.getIDProject();
                documentListDocument.setIDProject(project);
                documentListDocument = em.merge(documentListDocument);
                if (oldIDProjectOfDocumentListDocument != null) {
                    oldIDProjectOfDocumentListDocument.getDocumentList().remove(documentListDocument);
                    oldIDProjectOfDocumentListDocument = em.merge(oldIDProjectOfDocumentListDocument);
                }
            }
            for (Expense expenseListExpense : project.getExpenseList()) {
                Project oldIDProjectOfExpenseListExpense = expenseListExpense.getIDProject();
                expenseListExpense.setIDProject(project);
                expenseListExpense = em.merge(expenseListExpense);
                if (oldIDProjectOfExpenseListExpense != null) {
                    oldIDProjectOfExpenseListExpense.getExpenseList().remove(expenseListExpense);
                    oldIDProjectOfExpenseListExpense = em.merge(oldIDProjectOfExpenseListExpense);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Project project) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Project persistentProject = em.find(Project.class, project.getId());
            Task IDRootTaskOld = persistentProject.getIDRootTask();
            Task IDRootTaskNew = project.getIDRootTask();
            List<Income> incomeListOld = persistentProject.getIncomeList();
            List<Income> incomeListNew = project.getIncomeList();
            List<WorksOnProject> worksOnProjectListOld = persistentProject.getWorksOnProjectList();
            List<WorksOnProject> worksOnProjectListNew = project.getWorksOnProjectList();
            List<Document> documentListOld = persistentProject.getDocumentList();
            List<Document> documentListNew = project.getDocumentList();
            List<Expense> expenseListOld = persistentProject.getExpenseList();
            List<Expense> expenseListNew = project.getExpenseList();
            List<String> illegalOrphanMessages = null;
            for (WorksOnProject worksOnProjectListOldWorksOnProject : worksOnProjectListOld) {
                if (!worksOnProjectListNew.contains(worksOnProjectListOldWorksOnProject)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WorksOnProject " + worksOnProjectListOldWorksOnProject + " since its project field is not nullable.");
                }
            }
            for (Document documentListOldDocument : documentListOld) {
                if (!documentListNew.contains(documentListOldDocument)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Document " + documentListOldDocument + " since its IDProject field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (IDRootTaskNew != null) {
                IDRootTaskNew = em.getReference(IDRootTaskNew.getClass(), IDRootTaskNew.getId());
                project.setIDRootTask(IDRootTaskNew);
            }
            List<Income> attachedIncomeListNew = new ArrayList<Income>();
            for (Income incomeListNewIncomeToAttach : incomeListNew) {
                incomeListNewIncomeToAttach = em.getReference(incomeListNewIncomeToAttach.getClass(), incomeListNewIncomeToAttach.getId());
                attachedIncomeListNew.add(incomeListNewIncomeToAttach);
            }
            incomeListNew = attachedIncomeListNew;
            project.setIncomeList(incomeListNew);
            List<WorksOnProject> attachedWorksOnProjectListNew = new ArrayList<WorksOnProject>();
            for (WorksOnProject worksOnProjectListNewWorksOnProjectToAttach : worksOnProjectListNew) {
                worksOnProjectListNewWorksOnProjectToAttach = em.getReference(worksOnProjectListNewWorksOnProjectToAttach.getClass(), worksOnProjectListNewWorksOnProjectToAttach.getWorksOnProjectPK());
                attachedWorksOnProjectListNew.add(worksOnProjectListNewWorksOnProjectToAttach);
            }
            worksOnProjectListNew = attachedWorksOnProjectListNew;
            project.setWorksOnProjectList(worksOnProjectListNew);
            List<Document> attachedDocumentListNew = new ArrayList<Document>();
            for (Document documentListNewDocumentToAttach : documentListNew) {
                documentListNewDocumentToAttach = em.getReference(documentListNewDocumentToAttach.getClass(), documentListNewDocumentToAttach.getId());
                attachedDocumentListNew.add(documentListNewDocumentToAttach);
            }
            documentListNew = attachedDocumentListNew;
            project.setDocumentList(documentListNew);
            List<Expense> attachedExpenseListNew = new ArrayList<Expense>();
            for (Expense expenseListNewExpenseToAttach : expenseListNew) {
                expenseListNewExpenseToAttach = em.getReference(expenseListNewExpenseToAttach.getClass(), expenseListNewExpenseToAttach.getId());
                attachedExpenseListNew.add(expenseListNewExpenseToAttach);
            }
            expenseListNew = attachedExpenseListNew;
            project.setExpenseList(expenseListNew);
            project = em.merge(project);
            if (IDRootTaskOld != null && !IDRootTaskOld.equals(IDRootTaskNew)) {
                IDRootTaskOld.getProjectList().remove(project);
                IDRootTaskOld = em.merge(IDRootTaskOld);
            }
            if (IDRootTaskNew != null && !IDRootTaskNew.equals(IDRootTaskOld)) {
                IDRootTaskNew.getProjectList().add(project);
                IDRootTaskNew = em.merge(IDRootTaskNew);
            }
            for (Income incomeListOldIncome : incomeListOld) {
                if (!incomeListNew.contains(incomeListOldIncome)) {
                    incomeListOldIncome.setIDProject(null);
                    incomeListOldIncome = em.merge(incomeListOldIncome);
                }
            }
            for (Income incomeListNewIncome : incomeListNew) {
                if (!incomeListOld.contains(incomeListNewIncome)) {
                    Project oldIDProjectOfIncomeListNewIncome = incomeListNewIncome.getIDProject();
                    incomeListNewIncome.setIDProject(project);
                    incomeListNewIncome = em.merge(incomeListNewIncome);
                    if (oldIDProjectOfIncomeListNewIncome != null && !oldIDProjectOfIncomeListNewIncome.equals(project)) {
                        oldIDProjectOfIncomeListNewIncome.getIncomeList().remove(incomeListNewIncome);
                        oldIDProjectOfIncomeListNewIncome = em.merge(oldIDProjectOfIncomeListNewIncome);
                    }
                }
            }
            for (WorksOnProject worksOnProjectListNewWorksOnProject : worksOnProjectListNew) {
                if (!worksOnProjectListOld.contains(worksOnProjectListNewWorksOnProject)) {
                    Project oldProjectOfWorksOnProjectListNewWorksOnProject = worksOnProjectListNewWorksOnProject.getProject();
                    worksOnProjectListNewWorksOnProject.setProject(project);
                    worksOnProjectListNewWorksOnProject = em.merge(worksOnProjectListNewWorksOnProject);
                    if (oldProjectOfWorksOnProjectListNewWorksOnProject != null && !oldProjectOfWorksOnProjectListNewWorksOnProject.equals(project)) {
                        oldProjectOfWorksOnProjectListNewWorksOnProject.getWorksOnProjectList().remove(worksOnProjectListNewWorksOnProject);
                        oldProjectOfWorksOnProjectListNewWorksOnProject = em.merge(oldProjectOfWorksOnProjectListNewWorksOnProject);
                    }
                }
            }
            for (Document documentListNewDocument : documentListNew) {
                if (!documentListOld.contains(documentListNewDocument)) {
                    Project oldIDProjectOfDocumentListNewDocument = documentListNewDocument.getIDProject();
                    documentListNewDocument.setIDProject(project);
                    documentListNewDocument = em.merge(documentListNewDocument);
                    if (oldIDProjectOfDocumentListNewDocument != null && !oldIDProjectOfDocumentListNewDocument.equals(project)) {
                        oldIDProjectOfDocumentListNewDocument.getDocumentList().remove(documentListNewDocument);
                        oldIDProjectOfDocumentListNewDocument = em.merge(oldIDProjectOfDocumentListNewDocument);
                    }
                }
            }
            for (Expense expenseListOldExpense : expenseListOld) {
                if (!expenseListNew.contains(expenseListOldExpense)) {
                    expenseListOldExpense.setIDProject(null);
                    expenseListOldExpense = em.merge(expenseListOldExpense);
                }
            }
            for (Expense expenseListNewExpense : expenseListNew) {
                if (!expenseListOld.contains(expenseListNewExpense)) {
                    Project oldIDProjectOfExpenseListNewExpense = expenseListNewExpense.getIDProject();
                    expenseListNewExpense.setIDProject(project);
                    expenseListNewExpense = em.merge(expenseListNewExpense);
                    if (oldIDProjectOfExpenseListNewExpense != null && !oldIDProjectOfExpenseListNewExpense.equals(project)) {
                        oldIDProjectOfExpenseListNewExpense.getExpenseList().remove(expenseListNewExpense);
                        oldIDProjectOfExpenseListNewExpense = em.merge(oldIDProjectOfExpenseListNewExpense);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = project.getId();
                if (findProject(id) == null) {
                    throw new NonexistentEntityException("The project with id " + id + " no longer exists.");
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
            Project project;
            try {
                project = em.getReference(Project.class, id);
                project.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The project with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorksOnProject> worksOnProjectListOrphanCheck = project.getWorksOnProjectList();
            for (WorksOnProject worksOnProjectListOrphanCheckWorksOnProject : worksOnProjectListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Project (" + project + ") cannot be destroyed since the WorksOnProject " + worksOnProjectListOrphanCheckWorksOnProject + " in its worksOnProjectList field has a non-nullable project field.");
            }
            List<Document> documentListOrphanCheck = project.getDocumentList();
            for (Document documentListOrphanCheckDocument : documentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Project (" + project + ") cannot be destroyed since the Document " + documentListOrphanCheckDocument + " in its documentList field has a non-nullable IDProject field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Task IDRootTask = project.getIDRootTask();
            if (IDRootTask != null) {
                IDRootTask.getProjectList().remove(project);
                IDRootTask = em.merge(IDRootTask);
            }
            List<Income> incomeList = project.getIncomeList();
            for (Income incomeListIncome : incomeList) {
                incomeListIncome.setIDProject(null);
                incomeListIncome = em.merge(incomeListIncome);
            }
            List<Expense> expenseList = project.getExpenseList();
            for (Expense expenseListExpense : expenseList) {
                expenseListExpense.setIDProject(null);
                expenseListExpense = em.merge(expenseListExpense);
            }
            em.remove(project);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Project> findProjectEntities() {
        return findProjectEntities(true, -1, -1);
    }

    public List<Project> findProjectEntities(int maxResults, int firstResult) {
        return findProjectEntities(false, maxResults, firstResult);
    }

    private List<Project> findProjectEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Project.class));
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

    public Project findProject(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Project.class, id);
        } finally {
            em.close();
        }
    }

    public int getProjectCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Project> rt = cq.from(Project.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
