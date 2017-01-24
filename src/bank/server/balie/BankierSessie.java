package bank.server.balie;

import bank.interfaces.communication.IBankierSessie;
import bank.interfaces.domain.IBank;
import bank.interfaces.domain.IRekening;
import bank.server.domain.Money;
import fontys.util.InvalidSessionException;
import fontys.util.NumberDoesntExistException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BankierSessie extends UnicastRemoteObject implements
        IBankierSessie {

    private static final long serialVersionUID = 1L;
    private long laatsteAanroep;
    private int reknr;
    private IBank bank;

    public BankierSessie(int reknr, IBank bank) throws RemoteException {
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

        return bank.maakOver(reknr, bestemming, bedrag);
    }

    private void updateLaatsteAanroep() throws InvalidSessionException {
        if (!isGeldig()) {
            throw new InvalidSessionException("session has been expired");
        }

        laatsteAanroep = System.currentTimeMillis();
    }

    @Override
    public IRekening getRekening() throws InvalidSessionException,
            RemoteException {

        updateLaatsteAanroep();

        return bank.getRekening(reknr);
    }

    @Override
    public void logUit() throws RemoteException {
        UnicastRemoteObject.unexportObject(this, true);
    }

}
