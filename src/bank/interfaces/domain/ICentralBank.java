package bank.interfaces.domain;

import bank.domain.Money;
import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.IBankTransfer;
import bank.server.rmi.BalancePublisher;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by guill on 25-1-2017.
 */
public interface ICentralBank extends Remote {

    String getUniqueRekNr(String bankName) throws RemoteException;

    void registerBank(String bankName, IBankTransfer bank) throws RemoteException;

    boolean transfer(String from, String to, Money amount) throws RemoteException, NumberDoesntExistException;

    Map<String, IBankTransfer> getBanken();
}
