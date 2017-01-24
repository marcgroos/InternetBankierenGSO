/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.client;

import bank.interfaces.communication.IBalie;
import bank.interfaces.communication.IBankierSessie;
import bank.interfaces.domain.IRekening;
import bank.exceptions.InvalidSessionException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author frankcoenen
 */
public class LoginController implements Initializable {

    @FXML
    private TextField tfAccount;
    @FXML
    private TextField tfPassword;
    @FXML
    private Button btLogin;
    @FXML
    private Button btAccount;
    @FXML
    private TextArea taMessages;

    private IBalie balie;
    private IBankierSessie sessie;
    private BankierClient application;

    public void setApp(BankierClient application, IBalie balie, String AccountName) {
        this.balie = balie;
        this.application = application;
        this.tfAccount.setText(AccountName);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        taMessages.setText("");
        tfAccount.setPromptText("Acccount");
        tfPassword.setPromptText("Wachtwoord");
    }

    @FXML
    private void login(ActionEvent event) throws InvalidSessionException  {
        try {
            sessie = balie.logIn(tfAccount.getText(), tfPassword.getText());
            if (sessie == null) {
                taMessages.setText("accountname or password not correct");
            } else {
                IRekening r = sessie.getRekening();
                System.out.println("Rekening" + r.getSaldo());
                application.gotoBankierSessie(balie, sessie);
            }
        } catch (RemoteException e1) {
            taMessages.setText("bad connection with counter");
            e1.printStackTrace();
        }
    }

    @FXML
    private void openAccount(ActionEvent event) {
        application.gotoOpenRekening(balie);
    }

}
