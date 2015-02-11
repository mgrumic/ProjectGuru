/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.handlers;

import java.util.List;
import projectguru.entities.Document;
import projectguru.entities.DocumentRevision;
import projectguru.entities.Project;
import projectguru.handlers.exceptions.EntityDoesNotExistException;
import projectguru.handlers.exceptions.InsuficientPrivilegesException;
import projectguru.handlers.exceptions.StoringException;

/**
 *
 * @author ZM
 */
public interface DocumentHandler {

    public boolean checkPrivileges(Document document);

    public boolean addRevision(Document original, DocumentRevision revision) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException;

    public boolean addDocument(Project project, Document document, DocumentRevision revision) throws InsuficientPrivilegesException, StoringException;

    public boolean editDocument(Document document) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException;

    public boolean editRevision(DocumentRevision revision) throws EntityDoesNotExistException, InsuficientPrivilegesException, StoringException;

    public List<DocumentRevision> getRevisions(Document document) throws EntityDoesNotExistException;

}
