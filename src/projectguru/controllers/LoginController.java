/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import projectguru.AccessManager;
import projectguru.ProjectGuru;
import projectguru.handlers.LoggedUser;

/**
 * FXML Controller class
 *
 * @author ZM
 */
public class LoginController implements Initializable {

    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField tbPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnExit;

    @FXML
    private Label lblWarning;

    @FXML
    private ImageView imgViewLogo;

    /**
     * Moje varijable
     */
    private Stage stage;
   // private EventHandler<KeyEvent> enterLogin = new EventHandler<KeyEvent>() {
  //      @Override
  //      public void handle(KeyEvent arg0) {
   //         if (arg0.getCode() == KeyCode.ENTER) {
   //             login();
  //          }
  //      }
  //  };
    /**
     * FXML functions
     */
    @FXML
    void btnExitClicked(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void btnLoginClicked(ActionEvent event) {
        login();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblWarning.setVisible(false);
        imgViewLogo.setImage(new Image(getClass().getResourceAsStream("/projectguru/images/pg.png")));
       // tfUsername.setOnKeyPressed(enterLogin);
       // tbPassword.setOnKeyPressed(enterLogin);
        //btnLogin.setOnKeyPressed(enterLogin);
        btnExit.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent arg0) {
                if (arg0.getCode() == KeyCode.ENTER) {
                    btnExitClicked(new ActionEvent());
                }
            }
        });

    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    private void login() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lblWarning.setVisible(false);

                String username = tfUsername.getText();
                String password = tbPassword.getText();

                AccessManager access = AccessManager.getInstance();
                LoggedUser user = access.logUserIn(username, password);

                if (user != null) {
                    if (user.getUser().getActivated() == false) {
                        lblWarning.setText("Ваш налог је деактивиран.\nКонтактирајте администратора !");
                        lblWarning.setVisible(true);
                        tbPassword.setText("");
                        
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stage.hide();
                                try {
                                    new ProjectGuru().start(new Stage(), user);
                                } catch (IOException ex) {
                                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    }
                } else {
                    lblWarning.setText("Погрешно корисничко име или шифра");
                    lblWarning.setVisible(true);
                    tfUsername.setText("");
                    tbPassword.setText("");
                    tfUsername.requestFocus();
                }
            }
        });
    }

}
