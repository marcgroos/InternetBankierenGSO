/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.client;

import bank.exceptions.InvalidSessionException;
import bank.interfaces.communication.IBankProvider;
import bank.interfaces.communication.ISession;
import bank.interfaces.domain.IBankAccount;
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

    private IBankProvider balie;
    private ISession sessie;
    private BankingClient application;

    public void setApp(BankingClient application, IBankProvider balie, String AccountName) {
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
    private void login(ActionEvent event) throws InvalidSessionException {
        try {
            sessie = balie.logIn(tfAccount.getText(), tfPassword.getText());
            if (sessie == null) {
                taMessages.setText("accountname or password not correct");
            } else {
                IBankAccount r = sessie.getRekening();
                System.out.println("Rekening" + r.getBalance());
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
