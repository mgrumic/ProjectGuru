/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projectguru.tasktree;

/**
 *
 * @author marko
 */
public class TaskTree {
    protected TaskNode root;

    public TaskTree(TaskNode root) {
        this.root = root;
    }

    public TaskNode getRoot() {
        return root;
    }

    public void setRoot(TaskNode root) {
        this.root = root;
    }
    
    
}
