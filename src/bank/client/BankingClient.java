/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bank.client;

import bank.interfaces.communication.IBankProvider;
import bank.interfaces.communication.ISession;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Naming;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author frankcoenen
 */
public class BankingClient extends Application {

    private final double MINIMUM_WINDOW_WIDTH = 390.0;
    private final double MINIMUM_WINDOW_HEIGHT = 500.0;
    private Stage stage;
    //

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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected IBankProvider connectToBalie(String bankName) {
        try {
            FileInputStream in = new FileInputStream(bankName + ".props");
            Properties props = new Properties();
            props.load(in);
            String rmiBalie = props.getProperty("balie");
            in.close();

            IBankProvider balie = (IBankProvider) Naming.lookup("rmi://" + rmiBalie);
            return balie;

        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }

    protected void gotoBankSelect() {
        try {
            BankSelectController bankSelect = (BankSelectController) replaceSceneContent("fxml/BankSelect.fxml");
            bankSelect.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(BankingClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void gotoLogin(IBankProvider balie, String accountNaam) {
        try {
            LoginController login = (LoginController) replaceSceneContent("fxml/Login.fxml");
            login.setApp(this, balie, accountNaam);
        } catch (Exception ex) {
            Logger.getLogger(BankingClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void gotoOpenRekening(IBankProvider balie) {
        try {
            OpenRekeningController openRekeningController = (OpenRekeningController) replaceSceneContent("fxml/OpenRekening.fxml");
            openRekeningController.setApp(this, balie);
        } catch (Exception ex) {
            Logger.getLogger(BankingClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void gotoBankierSessie(IBankProvider balie, ISession sessie) {
        try {
            BankSessionController sessionController = (BankSessionController) replaceSceneContent("fxml/Session.fxml");
            sessionController.setApp(this, balie, sessie);
        } catch (Exception ex) {
            Logger.getLogger(BankingClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = BankingClient.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(BankingClient.class.getResource(fxml));
        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page, 800, 600);
        // scene.getStylesheets().add("bank/client/ING.css");
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

}
