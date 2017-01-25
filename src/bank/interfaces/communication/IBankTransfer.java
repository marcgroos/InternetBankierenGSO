package bank.interfaces.communication;

import bank.domain.Money;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by guill on 25-1-2017.
 */

public interface IBankTransfer extends Remote {
    boolean mutate(int nr, Money money) throws RemoteException;
}
