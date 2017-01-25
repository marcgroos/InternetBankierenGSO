/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.server;

import bank.client.BankingClient;
import bank.domain.Bank;
import bank.interfaces.communication.IBankProvider;
import bank.interfaces.domain.IBank;
import bank.interfaces.domain.ICentralBank;
import bank.server.balie.BankProvider;
import bank.server.rmi.BalancePublisher;
import bank.server.rmi.ConnConst;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author frankcoenen
 */
public class BankServer extends Application {

    private final double MINIMUM_WINDOW_WIDTH = 600.0;
    private final double MINIMUM_WINDOW_HEIGHT = 200.0;
    private Stage stage;

    public static BalancePublisher balancePublisher;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        try {
            stage = primaryStage;
            stage.setTitle("Bankieren");
            stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
            stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
            gotoBankSelect();

            primaryStage.show();

            // Create balance publisher
            balancePublisher = new BalancePublisher();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean startBalie(String bankName) {
        try {
            try {
                ICentralBank centralBank = getRemoteCentralBank();
                centralBank.registerBank(bankName, new Bank(centralBank, bankName));
                IBank bank = new Bank(getRemoteCentralBank(), bankName);
                IBankProvider bankProvider = new BankProvider(bank);

                Registry registry = LocateRegistry.createRegistry(ConnConst.BANK_SERVER_PORT);

                registry.rebind(bankName, bankProvider);

            } catch (NotBoundException e) {
                e.printStackTrace();
            }

            return true;

        } catch (IOException ex) {
            Logger.getLogger(BankServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private ICentralBank getRemoteCentralBank() throws RemoteException, NotBoundException, MalformedURLException {
        String url = "rmi://" + ConnConst.HOST_ADDRESS + ":" + ConnConst.CENTRAL_SERVER_PORT + "/" + ConnConst.CENTRAL_BANK_BINDING_NAME;
        return (ICentralBank) Naming.lookup(url);
    }

    public void gotoBankSelect() {
        try {
            BankController bankSelect = (BankController) replaceSceneContent("balie/fxml/BankProvider.fxml");
            bankSelect.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(BankingClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = BankServer.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(BankServer.class.getResource(fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page, 800, 600);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

}
