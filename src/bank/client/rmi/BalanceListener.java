package bank.client.rmi;

import bank.client.BankSessionController;
import bank.domain.Money;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BalanceListener extends UnicastRemoteObject implements IRemotePropertyListener {


    private static final String bindingName = "balancePublisher";

    private final BankSessionController bankSessionController;
    private IRemotePublisherForListener publisher;
    private Registry registry = null;

    private String ipAddress;
    private String propertyName;

    public BalanceListener(BankSessionController bankSessionController, String accountNr) throws RemoteException {
        this.ipAddress = "localhost";
        this.bankSessionController = bankSessionController;
        this.propertyName = accountNr;

        setUpListener();
    }

    public void setUpListener() {
        try {
            System.out.println("CLIENT: Trying to locate registry...");
            registry = LocateRegistry.getRegistry(ipAddress, 1069);
            System.out.println("CLIENT: Registry located");
        } catch (RemoteException ex) {
            System.out.println("CLIENT: Cannot locate registry");
            System.out.println("CLIENT: RemoteException: " + ex.getMessage());
            registry = null;
        }

        if (registry != null) {
            try {
                System.out.println("CLIENT: Trying to subscribe to '" + propertyName + "'...");
                publisher = (IRemotePublisherForListener) registry.lookup(bindingName);
                publisher.subscribeRemoteListener(this, propertyName);
            } catch (RemoteException | NotBoundException ex) {
                System.out.println("CLIENT: Cannot subscribe to '" + propertyName + "'");
                System.out.println("CLIENT: RemoteException: " + ex.getMessage());
                registry = null;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException {
        Money money = (Money) evt.getNewValue();
        bankSessionController.changeBalanceValue(money.getValue());
    }

}
