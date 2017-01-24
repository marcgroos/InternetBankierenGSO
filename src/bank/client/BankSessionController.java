/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.client;

import bank.exceptions.InvalidSessionException;
import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.IBankProvider;
import bank.interfaces.communication.ISession;
import bank.interfaces.domain.IBankAccount;
import bank.server.domain.Money;
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

    private BankingClient application;
    private IBankProvider balie;
    private ISession sessie;

    public void setApp(BankingClient application, IBankProvider balie, ISession sessie) {
        this.balie = balie;
        this.sessie = sessie;
        this.application = application;
        IBankAccount rekening = null;
        try {
            rekening = sessie.getRekening();
            tfAccountNr.setText(rekening.getNr() + "");
            tfBalance.setText(rekening.getBalance() + "");
            String eigenaar = rekening.getOwner().getNaam() + " te "
                    + rekening.getOwner().getPlaats();
            tfNameCity.setText(eigenaar);
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
            sessie.maakOver(to, new Money(centen, Money.EURO));
        } catch (RemoteException e1) {
            e1.printStackTrace();
            taMessage.setText("verbinding verbroken");
        } catch (NumberDoesntExistException | InvalidSessionException e1) {
            e1.printStackTrace();
            taMessage.setText(e1.getMessage());
        }
    }
}
