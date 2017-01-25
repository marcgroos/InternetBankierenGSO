package bank.interfaces.domain;

import bank.domain.Money;
import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.IBankTransfer;
import bank.server.rmi.BalancePublisher;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by guill on 25-1-2017.
 */
public interface ICentralBank extends Remote {

    int getUniqueRekNr(String bankName) throws RemoteException;

    void registerBank(String bankName, IBankTransfer bank) throws RemoteException;

    boolean transfer(int from, int to, Money amount) throws RemoteException, NumberDoesntExistException;
}
