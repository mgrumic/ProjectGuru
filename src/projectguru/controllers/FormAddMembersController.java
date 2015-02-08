/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import projectguru.controllers.TeamOfficeController.UserWrapper;

/**
 * FXML Controller class
 *
 * @author Marko
 */
public class FormAddMembersController implements Initializable {

    @FXML
    private AnchorPane page2;
    @FXML
    private ListView<UserWrapper> listViewSelectedMembers;
    @FXML
    private ListView<UserWrapper> listViewAllMembers;
    @FXML
    private Button buttonSelectAll;
    @FXML
    private Button buttonSelectOne;
    @FXML
    private Button buttonUnselectOne;
    @FXML
    private Button buttonUnselectAll;

    private ObservableList<UserWrapper> allMembers;
    private ObservableList<UserWrapper> selectedMembers;

    public FormAddMembersController(ObservableList<TeamOfficeController.UserWrapper> allMembers, ObservableList<TeamOfficeController.UserWrapper> selectedMembers) {
        this.allMembers = allMembers;
        this.selectedMembers = selectedMembers;
    }
    
    private final EventHandler<MouseEvent> eventOnSelectAll = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            selectedMembers.addAll(allMembers);
            allMembers.clear();
        }
    };
    private final EventHandler<MouseEvent> eventOnSelectOne = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            selectedMembers.addAll(listViewAllMembers.getSelectionModel().getSelectedItems());
            allMembers.removeAll(listViewAllMembers.getSelectionModel().getSelectedItems());
        }
    };
    private final EventHandler<MouseEvent> eventOnUnselectAll = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            allMembers.addAll(selectedMembers);
            selectedMembers.clear();
        }
    };
    private final EventHandler<MouseEvent> eventOnUnselectOne = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            selectedMembers.removeAll(listViewSelectedMembers.getSelectionModel().getSelectedItems());
            allMembers.addAll(listViewSelectedMembers.getSelectionModel().getSelectedItems());
        }
    };

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listViewSelectedMembers.setItems(selectedMembers);
        listViewAllMembers.setItems(allMembers);
        buttonSelectAll.setOnMouseClicked(eventOnSelectAll);
        buttonSelectOne.setOnMouseClicked(eventOnSelectOne);
        buttonUnselectAll.setOnMouseClicked(eventOnUnselectAll);
        buttonUnselectOne.setOnMouseClicked(eventOnUnselectOne);
        listViewAllMembers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewSelectedMembers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setListViewSelectedMembers(ListView<UserWrapper> listViewSelectedMembers) {
        this.listViewSelectedMembers = listViewSelectedMembers;
    }

    public void setListViewAllMembers(ListView<UserWrapper> listViewAllMembers) {
        this.listViewAllMembers = listViewAllMembers;
    }

    public ListView<UserWrapper> getListViewSelectedMembers() {
        return listViewSelectedMembers;
    }

    public ListView<UserWrapper> getListViewAllMembers() {
        return listViewAllMembers;
    }

}
