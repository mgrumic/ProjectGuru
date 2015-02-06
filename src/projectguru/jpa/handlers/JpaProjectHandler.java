/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.jpa.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import projectguru.AccessManager;
import projectguru.entities.Activity;
import projectguru.entities.Document;
import projectguru.entities.Expense;
import projectguru.entities.Income;
import projectguru.entities.Privileges;
import projectguru.entities.Project;
import projectguru.entities.Task;
import projectguru.entities.User;
import projectguru.entities.WorksOnProject;
import projectguru.entities.WorksOnProjectPK;
import projectguru.entities.WorksOnTask;
import projectguru.entities.WorksOnTaskPK;
import projectguru.handlers.LoggedUser;
import projectguru.handlers.ProjectHandler;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;
import projectguru.jpa.JpaAccessManager;

/**
 *
 * @author ZM
 */
public class JpaProjectHandler implements ProjectHandler {

    private LoggedUser loggedUser;

    public JpaProjectHandler(LoggedUser user) {
        this.loggedUser = user;
    }

    @Override
    public boolean checkProjectChefPrivileges(Project project) {
        return isChef(project, loggedUser.getUser());
    }

    @Override
    public boolean checkMemberPrivileges(Project project) {
        return isMember(project, loggedUser.getUser());
    }

    @Override
    public boolean checkInsightPrivileges(Project project) {
        return isInsight(project, loggedUser.getUser());
    }

    @Override
    public List<Project> getAllProjects() {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {

            TypedQuery<Project> query = em.createQuery("SELECT wop.project FROM User u, "
                    + "IN(u.worksOnProjectList) wop WHERE u.username = :username", Project.class);

            query.setParameter("username", loggedUser.getUser().getUsername());

            return query.getResultList();

        } catch (Exception ex) {

        } finally {
            em.close();
        }
        return new ArrayList<Project>();
    }

    @Override
    public List<User> getAllMembers(Project project) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {

            TypedQuery<User> query = em.createQuery("SELECT wop.user FROM Project p, IN(p.worksOnProjectList) wop WHERE p.id = :id", User.class);
            
            query.setParameter("id", project.getId());

            return query.getResultList();

        } catch (Exception ex) {

        } finally {
            em.close();
        }
        return new ArrayList<User>();
    }
    
    @Override
    public List<User> getAllChefs(Project project) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {

            TypedQuery<User> query = em.createQuery("SELECT wop.user FROM Project p, IN(p.worksOnProjectList) wop WHERE p.id = :id "
                    + "and wop.privileges = :privileges", User.class);
            
            query.setParameter("id", project.getId());
            query.setParameter("privileges", Privileges.CHEF.ordinal());
            
           // System.out.println(Privileges.CHEF.ordinal());
            
            return query.getResultList();

        } catch (Exception ex) {
            System.out.println("Exception !");
        } finally {
            em.close();
        }
        return new ArrayList<User>();
    }
    

    @Override
    public boolean createProject(Project project) throws InsuficientPrivilegesException, StoringException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            if (project.getId() == null || (project = em.find(Project.class, project.getId())) == null) {
                em.getTransaction().begin();
                em.persist(project);
                em.getTransaction().commit();

                addMember(project, loggedUser.getUser());
                setPrivileges(project, loggedUser.getUser(), Privileges.CHEF);

                return true;
            }
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new StoringException(ex.getLocalizedMessage());
        } finally {
            em.close();
        }
        return false;
    }

    /*
     *
     *   Ova funkcija zahtjeva da je proslijedjeni objekat azuriran
     *   Savjet je da se prije poziva ove funkcije dobavi azuriran projekat
     *   pomocu funkcije getUpdatedProject(Project project) i da se on izmjeni pa 
     *   da se onda edituje !
     */
    @Override
    public boolean editProject(Project project) throws InsuficientPrivilegesException, EntityDoesNotExistException, StoringException {

        if (!checkProjectChefPrivileges(project)) {
            throw new InsuficientPrivilegesException();
        }
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (project.getId() == null || (em.find(Project.class, project.getId())) == null) {
            throw new EntityDoesNotExistException("Project does not exist.");
        }
        try {

            em.getTransaction().begin();
            em.merge(project);
            em.getTransaction().commit();

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
    public boolean deleteProject(Project project) throws InsuficientPrivilegesException, EntityDoesNotExistException, StoringException {

        if (!checkProjectChefPrivileges(project)) {
            throw new InsuficientPrivilegesException();
        }
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (project.getId() == null || (project = em.find(Project.class, project.getId())) == null) {
            throw new EntityDoesNotExistException("Project does not exist.");
        }
        try {

            em.getTransaction().begin();
            em.remove(project);
            em.getTransaction().commit();

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

    public void setRootTask(Project project, Task task) throws InsuficientPrivilegesException, EntityDoesNotExistException, StoringException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (project.getId() == null || (project = em.find(Project.class, project.getId())) == null) {
            throw new EntityDoesNotExistException("Project does not exist.");
        }
        try {
            em.getTransaction().begin();

            Task check = null;
            if (task.getId() == null || (check = em.find(Task.class, task.getId())) == null) {
                task.setProjectList(new ArrayList<Project>());
                task.getProjectList().add(project);
                em.persist(task);
                em.flush();
                project.setIDRootTask(task);
                
                //treba dodati clanove projekta u root zadatak
                
                for(WorksOnProject wop : project.getWorksOnProjectList()){
                    
                    WorksOnTask wot = new WorksOnTask();
                    wot.setWorksOnTaskPK(new WorksOnTaskPK());
                    wot.getWorksOnTaskPK().setIDTask(task.getId());
                    wot.getWorksOnTaskPK().setUsername(wop.getWorksOnProjectPK().getUsername());
                    wot.getWorksOnTaskPK().setIDProject(project.getId());
                    wot.setPrivileges(wop.getPrivileges());
                    wot.setWorking(false);
                    
                    task.getWorksOnTaskList().add(wot);
                }
                

            } else {
                project.setIDRootTask(check);
            }
            em.getTransaction().commit();
            em.refresh(project);
            em.refresh(task);

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();

            throw new StoringException(ex.getLocalizedMessage());
        } finally {
            em.close();
        }
    }

    //Izbrisao sam metodu setChef, jer kako da setujem ostale privilegije, moram praviti drugu metodu
    //Bolje da napravim jednu metodu koja to sve radi !
    @Override
    public boolean setPrivileges(Project project, User user, Privileges privileges) throws StoringException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            
            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
            
            if(user.getUsername()== null || (user = em.find(user.getClass(), user.getUsername())) == null){
                throw new EntityDoesNotExistException("User does not exist.");
            }
            
            em.getTransaction().begin();

            TypedQuery<WorksOnProject> query = em.createQuery("SELECT wop FROM WorksOnProject wop WHERE "
                    + "wop.worksOnProjectPK.username = :username AND "
                    + "wop.worksOnProjectPK.iDProject = :iDProject", WorksOnProject.class);

            query.setParameter("username", user.getUsername());
            query.setParameter("iDProject", project.getId());

            List<WorksOnProject> wop = query.getResultList();

            if (wop.isEmpty()) {
                em.getTransaction().commit();
                return false;
            } else if (wop.size() == 1) {
                wop.get(0).setPrivileges(privileges.ordinal());
                em.merge(wop.get(0));
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();

            throw new StoringException(ex.getLocalizedMessage());

        } finally {
            em.close();
        }
    }

    /*
     *  NAPOMENA: Kod dodavanja clanova, privilegije se postavljau na pocetku na NO_PRIVILEGES
     *  Tako da se poslije dodavanja trebaju setovati koristenjem setPrivileges
     */
    @Override
    public boolean addMember(Project project, User user) throws StoringException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
            
            if(user.getUsername()== null || (user = em.find(user.getClass(), user.getUsername())) == null){
                throw new EntityDoesNotExistException("User does not exist.");
            }

            em.getTransaction().begin();

            TypedQuery<WorksOnProject> query = em.createQuery("SELECT wop FROM WorksOnProject wop WHERE "
                    + "wop.worksOnProjectPK.username = :username AND "
                    + "wop.worksOnProjectPK.iDProject = :iDProject", WorksOnProject.class);

            query.setParameter("username", user.getUsername());
            query.setParameter("iDProject", project.getId());

            //Ne koristim query.getSingleResult() jer baca NoResultException, a ne volim ih :D
            List<WorksOnProject> wop = query.getResultList();

            if (wop.isEmpty()) {

                WorksOnProject newWop = new WorksOnProject(new WorksOnProjectPK(user.getUsername(), project.getId()));
                newWop.setPrivileges(Privileges.NO_PRIVILEGES.ordinal());
                em.persist(newWop);
                em.getTransaction().commit();
                em.refresh(project);
                em.refresh(user);

            } else if (wop.size() == 1) {

                WorksOnProject oldWop = wop.get(0);
                em.merge(oldWop);
                em.getTransaction().commit();

            } else {
                return false;
            }
            return true;

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();

            throw new StoringException(ex.getLocalizedMessage());

        } finally {
            em.close();
        }
    }

    @Override
    public boolean addDocument(Project project, Document document) throws InsuficientPrivilegesException, StoringException {

        if (!checkMemberPrivileges(project)) {
            throw new InsuficientPrivilegesException();
        }
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {

            em.getTransaction().begin();

            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
       
            document.setIDProject(project);

            em.persist(document);
            em.getTransaction().commit();

            return true;

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();

            throw new StoringException(ex.getLocalizedMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public List<Document> getAllDocuments(Project project) throws EntityDoesNotExistException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }


            return project.getDocumentList();
            //Moze i ovako (za slucaj da ovo gore ne radi kako treba):
            //return em.createNamedQuery("Document.findByProjectID", Document.class)
            //        .setParameter("iDProject", project.getId()).getResultList();

        } catch (EntityDoesNotExistException ex) {
            throw new EntityDoesNotExistException(ex.getLocalizedMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public boolean addExpense(Project project, Expense exp) throws StoringException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {

            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }

            exp.setIDProject(project);

            em.getTransaction().begin();
            em.persist(exp);
            em.getTransaction().commit();

            em.refresh(project);
            return true;
        } catch (Exception ex) {

        } finally {
            em.close();
        }
        return false;
    }

    @Override
    public boolean addIncome(Project project, Income inc) throws StoringException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {

            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
            
            inc.setIDProject(project);

            em.getTransaction().begin();
            em.persist(inc);
            em.getTransaction().commit();

            em.refresh(project);
            return true;
        } catch (Exception ex) {

        } finally {
            em.close();
        }
        return false;
    }

    /*
     *  Metode koje sam ja dodao
     *
     */
    public boolean isChef(Project project, User user) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        try {

            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
            
            if(user.getUsername()== null || (user = em.find(user.getClass(), user.getUsername())) == null){
                throw new EntityDoesNotExistException("User does not exist.");
            }

            for (WorksOnProject wop : project.getWorksOnProjectList()) {
                if (wop.getWorksOnProjectPK().getUsername().equals(user.getUsername())
                        && wop.getPrivileges() == Privileges.CHEF.ordinal()) {
                    return true;
                }
            }
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(JpaProjectHandler.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            em.close();
        }
        return false;
    }

    //Da li postoji red u tabeli WORKS_ON_PROJECT izmedju korisnika i projekta
    public boolean connectionExists(Project project, User user) {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        try {
            
            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
            
            if(user.getUsername()== null || (user = em.find(user.getClass(), user.getUsername())) == null){
                throw new EntityDoesNotExistException("User does not exist.");
            }

            TypedQuery<WorksOnProject> query = em.createQuery("SELECT wop FROM WorksOnProject wop WHERE "
                    + "wop.worksOnProjectPK.username = :username AND "
                    + "wop.worksOnProjectPK.iDProject = :iDProject", WorksOnProject.class);

            query.setParameter("username", user.getUsername());
            query.setParameter("iDProject", project.getId());

            List<WorksOnProject> wop = query.getResultList();

            if (wop.size() == 0) {
                return false;
            } else {
                //mozda sam samo trebao da vratim true ako postoji tacno jedna !
                return true;
            }
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(JpaProjectHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isMember(Project project, User user) {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        
        try {
            
            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
            
            if(user.getUsername()== null || (user = em.find(user.getClass(), user.getUsername())) == null){
                throw new EntityDoesNotExistException("User does not exist.");
            }
            
            for (WorksOnProject wop : project.getWorksOnProjectList()) {
                if (wop.getWorksOnProjectPK().getUsername().equals(user.getUsername())
                        && wop.getPrivileges() >= Privileges.MEMBER.ordinal()) {
                    return true;
                }
            }
            return false;
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(JpaProjectHandler.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            em.close();
        }
        return false;
    }

    public boolean isInsight(Project project, User user) {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();
        
        try {
            if(project.getId() == null || (project = em.find(project.getClass(), project.getId())) == null){
                throw new EntityDoesNotExistException("Project does not exist.");
            }
            
            if(user.getUsername()== null || (user = em.find(user.getClass(), user.getUsername())) == null){
                throw new EntityDoesNotExistException("User does not exist.");
            }
            
            for (WorksOnProject wop : project.getWorksOnProjectList()) {
                if (wop.getWorksOnProjectPK().getUsername().equals(user.getUsername())
                        && wop.getPrivileges() >= Privileges.INSIGHT.ordinal()) {
                    return true;
                }
            }
        } catch (EntityDoesNotExistException ex) {
            Logger.getLogger(JpaProjectHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /*
     *  Metode koje mi uvijek daju azurirane objekte iz baze
     *
     */
    public Project getUpdatedProject(Project project) throws EntityDoesNotExistException {

        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (project.getId() == null || (project = em.find(Project.class, project.getId())) == null) {
            throw new EntityDoesNotExistException("Failed to load user !");
        }
        em.close();
        return project;
    }

    public User getUpdatedUser(User user) throws EntityDoesNotExistException {
        EntityManagerFactory emf = ((JpaAccessManager) AccessManager.getInstance()).getFactory();
        EntityManager em = emf.createEntityManager();

        if (user.getUsername() == null || (user = em.find(User.class, user.getUsername())) == null) {
            throw new EntityDoesNotExistException("Failed to load user !");
        }

        em.close();
        return user;
    }
    
    

}
