package bank.server;

import bank.domain.CentralBank;
import bank.server.rmi.BalancePublisher;
import bank.server.rmi.ConnConst;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Created by guill on 25-1-2017.
 */
public class CentralBankServer {
    public static void main(String[] arg) {

        try{
            CentralBank bankCentrale = new CentralBank();
            System.out.println("CentralBank has started");

            Registry registry = LocateRegistry.createRegistry(ConnConst.CENTRAL_SERVER_PORT);
            registry.rebind(ConnConst.CENTRAL_BANK_BINDING_NAME, bankCentrale);

            System.out.println("CentralBank is bound in the registry");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Waiting to close
        new Scanner(System.in).next();
        System.out.println("CentralBank is closing");

    }
}
