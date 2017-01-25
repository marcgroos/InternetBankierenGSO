package bank.server.balie;

import bank.domain.Money;
import bank.exceptions.InvalidSessionException;
import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.ISession;
import bank.interfaces.domain.IBank;
import bank.interfaces.domain.IBankAccount;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Session extends UnicastRemoteObject implements
        ISession {

    private static final long serialVersionUID = 1L;
    private long lastCall;
    private int accountNr;
    private IBank bank;

    public Session(int accountNr, IBank bank) throws RemoteException {
        lastCall = System.currentTimeMillis();
        this.accountNr = accountNr;
        this.bank = bank;
    }

    public boolean timeLimitExceeded() {
        return System.currentTimeMillis() - lastCall >= TIME_LIMIT;
    }

    @Override
    public boolean transferMoney(int bestemming, Money bedrag)
            throws NumberDoesntExistException, InvalidSessionException,
            RemoteException {

        updateLastCall();

        if (accountNr == bestemming)
            throw new RuntimeException(
                    "source and destination must be different");
        if (!bedrag.isPositive())
            throw new RuntimeException("amount must be positive");

        return bank.transferMoney(accountNr, bestemming, bedrag);
    }

    private void updateLastCall() throws InvalidSessionException {
        if (timeLimitExceeded()) {
//            bank.getBankAccount(accountNr).
            throw new InvalidSessionException("session has been expired");
        }

        lastCall = System.currentTimeMillis();
    }

    @Override
    public IBankAccount getRekening() throws InvalidSessionException,
            RemoteException {

        updateLastCall();

        return bank.getBankAccount(accountNr);
    }

    @Override
    public void logUit() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
    }

}
