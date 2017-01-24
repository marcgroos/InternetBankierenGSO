package bank.server.balie;

import bank.exceptions.InvalidSessionException;
import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.ISession;
import bank.interfaces.domain.IBank;
import bank.interfaces.domain.IBankAccount;
import bank.server.domain.Money;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Session extends UnicastRemoteObject implements
        ISession {

    private static final long serialVersionUID = 1L;
    private long laatsteAanroep;
    private int reknr;
    private IBank bank;

    public Session(int reknr, IBank bank) throws RemoteException {
        laatsteAanroep = System.currentTimeMillis();
        this.reknr = reknr;
        this.bank = bank;
    }

    public boolean isGeldig() {
        return System.currentTimeMillis() - laatsteAanroep < GELDIGHEIDSDUUR;
    }

    @Override
    public boolean maakOver(int bestemming, Money bedrag)
            throws NumberDoesntExistException, InvalidSessionException,
            RemoteException {

        updateLaatsteAanroep();

        if (reknr == bestemming)
            throw new RuntimeException(
                    "source and destination must be different");
        if (!bedrag.isPositive())
            throw new RuntimeException("amount must be positive");

        return bank.transferMoney(reknr, bestemming, bedrag);
    }

    private void updateLaatsteAanroep() throws InvalidSessionException {
        if (!isGeldig()) {
            throw new InvalidSessionException("session has been expired");
        }

        laatsteAanroep = System.currentTimeMillis();
    }

    @Override
    public IBankAccount getRekening() throws InvalidSessionException,
            RemoteException {

        updateLaatsteAanroep();

        return bank.getBankAccount(reknr);
    }

    @Override
    public void logUit() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
    }

}
