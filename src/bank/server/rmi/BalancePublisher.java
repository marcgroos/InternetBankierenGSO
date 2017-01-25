package bank.server.rmi;

import bank.interfaces.domain.IBankAccount;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.RemotePublisher;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Set;

/**
 * Created by guill on 4-10-2016.
 */
public class BalancePublisher extends RemotePublisher implements Serializable, Remote {

    private Registry registry = null;
    private List<String> propertyNames;

    public BalancePublisher() throws RemoteException {
        super(new String[]{});
//        UnicastRemoteObject.exportObject(this, 0);

        this.propertyNames = new ArrayList<>();

        createRegistry();
        bindPublisher();
//        registerProperty();
    }

    //
    private void createRegistry() {
        // Create registry.
        try {
            System.out.println("SERVER: Trying to create registry on port " + ConnectionConstants.PUBLISHER_PORT + "...");
            registry = LocateRegistry.createRegistry(ConnectionConstants.PUBLISHER_PORT);
            System.out.println("SERVER: Registry created on port " + ConnectionConstants.PUBLISHER_PORT);
        } catch (RemoteException ex) {
            System.out.println("SERVER: FAILED to create Registry. There will be no publisher.");
            return;
        }
    }

    //
    private void bindPublisher() {
        // Bind publisher.
        try {
            System.out.println("SERVER: Trying to bind publisher...");
            registry.rebind(ConnectionConstants.PUBLISHER_BINDING_NAME, this);
            System.out.println("SERVER: RemotePublisher registered as " + ConnectionConstants.PUBLISHER_BINDING_NAME + " on port " + ConnectionConstants.PUBLISHER_PORT);
        } catch (RemoteException ex) {
            System.out.println("SERVER: FAILED to bind the publisher in the Registry.");
            return;
        }
    }

    public void registerProperty(IBankAccount bankAccount) {
        // Register koers property.
        String propertyName = bankAccount.getNr() + "";

        try {
            System.out.println("SERVER: Trying to register '" + propertyName + "'...");
            this.registerProperty(propertyName + "");
            System.out.println("SERVER: Successfully registered '" + propertyName + "'.");

            propertyNames.add(propertyName);

        } catch (RemoteException ex) {
            System.out.println("SERVER: FAILED to register '" + propertyName + "'.");
        }
    }

    public void informBalance(IBankAccount bankAccount) {

        String propertyName = bankAccount.getNr() + "";

        System.out.println("SERVER: Trying to send '" + propertyName + "'... ");
        try {
            super.inform(propertyName, null, bankAccount.getBalance());
            System.out.println("SERVER: '" + propertyName + "' successfully sent.");
        } catch (Exception ex) {
            System.out.println("SERVER: FAILED to send '" + propertyName + "'.");
        }
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }
}
