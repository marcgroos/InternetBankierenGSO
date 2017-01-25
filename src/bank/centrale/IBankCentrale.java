package bank.centrale;

import bank.domain.Money;
import bank.exceptions.NumberDoesntExistException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by guill on 25-1-2017.
 */
public interface IBankCentrale extends Remote {

    int getUniqueRekNr(String bankName) throws RemoteException;
    void registerBank(String banName, IBankTransfer bank) throws RemoteException;
    boolean transfer(int from, int to, Money amount) throws RemoteException, NumberDoesntExistException;

}
