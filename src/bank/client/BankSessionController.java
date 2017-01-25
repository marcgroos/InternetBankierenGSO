/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.client;

import bank.domain.Money;
import bank.exceptions.InvalidSessionException;
import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.IBankProvider;
import bank.interfaces.communication.ISession;
import bank.interfaces.domain.IBankAccount;
import bank.server.rmi.BalanceListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author frankcoenen
 */
public class BankSessionController implements Initializable {

    @FXML
    private Hyperlink hlLogout;

    @FXML
    private TextField tfNameCity;
    @FXML
    private TextField tfAccountNr;
    @FXML
    private TextField tfBalance;
    @FXML
    private TextField tfAmount;
    @FXML
    private TextField tfToAccountNr;
    @FXML
    private Button btTransfer;
    @FXML

    private TextArea taMessage;

    private BalanceListener balanceListener;
    private BankingClient application;
    private IBankProvider balie;
    private ISession sessie;

    public void setApp(BankingClient application, IBankProvider bankProvider, ISession session) {
        this.balie = bankProvider;
        this.sessie = session;
        this.application = application;

        IBankAccount bankAccount = null;

        try {
            bankAccount = session.getRekening();
            tfAccountNr.setText(bankAccount.getNr() + "");
            tfBalance.setText(bankAccount.getBalance() + "");
            String eigenaar = bankAccount.getOwner().getName() + " te " + bankAccount.getOwner().getCity();
            tfNameCity.setText(eigenaar);

            this.balanceListener = new BalanceListener(this, bankAccount);

        } catch (InvalidSessionException ex) {
            taMessage.setText("bankiersessie is verlopen");
            Logger.getLogger(BankSessionController.class.getName()).log(Level.SEVERE, null, ex);

        } catch (RemoteException ex) {
            taMessage.setText("verbinding verbroken");
            Logger.getLogger(BankSessionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            sessie.logUit();
            application.gotoLogin(balie, "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void transfer(ActionEvent event) {
        try {
            int from = Integer.parseInt(tfAccountNr.getText());
            int to = Integer.parseInt(tfToAccountNr.getText());
            if (from == to) {
                taMessage.setText("can't transferMoney money to your own account");
            }
            long centen = (long) (Double.parseDouble(tfAmount.getText()) * 100);
            sessie.transferMoney(to, new Money(centen, Money.EURO));
        } catch (RemoteException e1) {
            e1.printStackTrace();
            taMessage.setText("verbinding verbroken");
        } catch (NumberDoesntExistException | InvalidSessionException e1) {
            e1.printStackTrace();
            taMessage.setText(e1.getMessage());
        }
    }

    public void changeBalanceValue(String newValue) {
        this.tfBalance.setText(newValue);
    }
}
