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

/**
 *
 * @author ZM
 */
public interface DocumentHandler {
       
    public boolean addRevision(Document original, DocumentRevision revision);
    public boolean editDocument(Document document);
    public boolean editRevision(DocumentRevision revision);
    
    public List<DocumentRevision> getRevisions(Document document);
    
}
