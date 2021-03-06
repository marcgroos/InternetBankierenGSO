package bank.server.rmi;

import bank.server.BalieServer;
import bank.domain.Money;
import bank.server.rmi.observer.BasicPublisher;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by guill on 4-10-2016.
 */
public class BalancePublisher extends BasicPublisher implements Serializable, Remote {

    private String propertyName;
//    private Registry registry = null;

    public BalancePublisher(int accountNr) throws RemoteException {
        super(new String[]{});
        UnicastRemoteObject.exportObject(this, 0);

        this.propertyName = accountNr + "";
        this.addProperty(this.propertyName);

//        createRegistry();
//        bindPublisher();
//        registerProperty();
    }
//
//    private void createRegistry() {
//        // Create registry.
//        try {
//            System.out.println("SERVER: Trying to create registry on port " + BalieServer.RMI_PORT + "...");
//            registry = LocateRegistry.createRegistry(BalieServer.RMI_PORT);
//            System.out.println("SERVER: Registry created on port " + BalieServer.RMI_PORT);
//        } catch (RemoteException ex) {
//            System.out.println("SERVER: FAILED to create Registry. There will be no publisher.");
//            return;
//        }
//    }
//
//    private void bindPublisher() {
//        // Bind publisher.
//        try {
//            System.out.println("SERVER: Trying to bind publisher...");
//            registry.rebind(bindingName, this);
//            System.out.println("SERVER: RemotePublisher registered as " + bindingName + " on port " + BalieServer.RMI_PORT);
//        } catch (RemoteException ex) {
//            System.out.println("SERVER: FAILED to bind the publisher in the Registry.");
//            return;
//        }
//    }

//    private void registerProperty() {
//        // Register koers property.
//        try {
//            System.out.println("SERVER: Trying to register '" + propertyName + "'...");
//            this.registerProperty(propertyName);
//            System.out.println("SERVER: Successfully registered '" + propertyName + "'.");
//        } catch (RemoteException ex) {
//            System.out.println("SERVER: FAILED to register '" + propertyName + "'.");
//        }
//    }

    public void informBalance(Object oldMoney, Object newMoney) {

        System.out.println("SERVER: Trying to send '" + propertyName + "'... ");
        try {
            super.inform(this, propertyName, oldMoney, newMoney);
            System.out.println("SERVER: '" + propertyName + "' successfully sent.");
        } catch (Exception ex) {
            System.out.println("SERVER: FAILED to send '" + propertyName + "'.");
        }
    }
}
