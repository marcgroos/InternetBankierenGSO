package bank.domain;

import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.IBankTransfer;
import bank.interfaces.domain.ICentralBank;
import bank.server.rmi.BalancePublisher;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guill on 25-1-2017.
 */
public class CentralBank implements Serializable, ICentralBank, AutoCloseable {

    private Map<String, String> rekeningen = new HashMap<>();
    private Map<String, IBankTransfer> banken = new HashMap<>();

    private String prefix;

    public CentralBank() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
    }

    public Map<String, IBankTransfer> getBanken() {
        return banken;
    }

    @Override
    public String getUniqueRekNr(String bankName) throws RemoteException {

        prefix = bankName.substring(0, 3).toUpperCase();

        synchronized (rekeningen) {

            String out = prefix + "-" + (rekeningen.size() + 1);
            rekeningen.put(out, bankName);

            return out;
        }
    }

    @Override
    public void registerBank(String bankName, IBankTransfer bank) throws RemoteException {
        banken.put(bankName, bank);
    }

    @Override
    public boolean transfer(String from, String to, Money amount) throws RemoteException, NumberDoesntExistException {
        IBankTransfer src = getBankFromAccountNumber(from);
        IBankTransfer dst = getBankFromAccountNumber(to);

        Money negative = Money.difference(new Money(0, amount.getCurrency()), amount);
        boolean success = src.mutate(from, negative);
        if (!success) return false;

        success = dst.mutate(to, amount);

        if (!success) {
            src.mutate(from, amount);
        }

        return success;
    }

    IBankTransfer getBankFromAccountNumber(String rekNr) throws NumberDoesntExistException {
        String bankName = rekeningen.get(rekNr);
        if (bankName == null) throw new NumberDoesntExistException("account" + rekNr + "is unknown");
        return banken.get(bankName);
    }

    @Override
    public void close() throws Exception {
        UnicastRemoteObject.unexportObject(this, true);
    }
}
