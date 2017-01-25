package bank.server.rmi;

import bank.client.BankSessionController;
import bank.domain.Money;
import bank.interfaces.domain.IBankAccount;
import bank.server.rmi.BalancePublisher;
import bank.server.rmi.ConnectionConstants;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class BalanceListener extends UnicastRemoteObject implements IRemotePropertyListener {

    private final BankSessionController bankSessionController;
    private IBankAccount bankAccount;
    private IRemotePublisherForListener publisher;
    private Registry registry = null;

    public BalanceListener(BankSessionController bankSessionController, IBankAccount bankAccount) throws RemoteException {
        this.bankSessionController = bankSessionController;
        this.bankAccount = bankAccount;
        setUpListener();
    }

    public void setUpListener() {
        try {
            System.out.println("CLIENT: Trying to locate registry...");
            registry = LocateRegistry.getRegistry(ConnectionConstants.HOST_ADDRESS, ConnectionConstants.PUBLISHER_PORT);
            System.out.println("CLIENT: Registry located");
        } catch (RemoteException ex) {
            System.out.println("CLIENT: Cannot locate registry");
            System.out.println("CLIENT: RemoteException: " + ex.getMessage());
            registry = null;
        }

        if (registry != null) {
            try {
                System.out.println("CLIENT: Trying to subscribe to '" + bankAccount.getNr() + "'...");
                publisher = (IRemotePublisherForListener) registry.lookup(ConnectionConstants.PUBLISHER_BINDING_NAME);
                publisher.subscribeRemoteListener(this, bankAccount.getNr() + "");
            } catch (RemoteException | NotBoundException ex) {
                System.out.println("CLIENT: Cannot subscribe to '" + bankAccount.getNr() + "'");
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
