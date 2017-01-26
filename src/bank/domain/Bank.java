package bank.domain;


import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.communication.IBankTransfer;
import bank.interfaces.domain.*;
import bank.server.BankServer;
import bank.server.CentralBankServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Bank implements Serializable, IBank, IBankTransfer, AutoCloseable {

    /**
     *
     */
    private static final long serialVersionUID = -8728841131739353765L;
    private Map<String, IMutateable> accounts;
    private Collection<IUserAccount> clients;
    private String name;
    private ICentralBank centralBank;

    public Bank(ICentralBank bankCentrale, String name) throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);

        this.accounts = new HashMap<>();
        this.clients = new ArrayList<>();
        this.name = name;
        this.centralBank = bankCentrale;
    }

    public String openBankAccount(String name, String city) throws RemoteException {
        if (name.equals("") || city.equals(""))
            return null;

        String accountString = centralBank.getUniqueRekNr(this.name);

        IMutateable account = new BankAccount(accountString, getUserAccount(name, city), Money.EURO);
        accounts.put(accountString, account);

        // Register property
        BankServer.balancePublisher.registerProperty(account);

        return accountString;

/*        IUserAccount klant = getUserAccount(name, city);
        IMutateable account = new BankAccount(nieuwReknr, klant, Money.EURO);
        accounts.put(nieuwReknr, account);
        balancePublisher.registerProperty(account); // Add to balance publisher

        nieuwReknr++;
        return nieuwReknr - 1;*/
    }

    private IUserAccount getUserAccount(String name, String city) {
        for (IUserAccount k : clients) {
            if (k.getName().equals(name) && k.getCity().equals(city))
                return k;
        }
        IUserAccount klant = new UserAccount(name, city);
        clients.add(klant);
        return klant;
    }

    public IBankAccount getBankAccount(String nr) {
        return accounts.get(nr);
    }

    public boolean transferMoney(String source, String destination, Money money)
            throws NumberDoesntExistException {

        // Source is the same as destination.
        if (source == destination)
            throw new RuntimeException(
                    "cannot transferMoney money to your own account");

        // Negative money
        if (!money.isPositive())
            throw new RuntimeException("money must be positive");


        IMutateable source_account = (IMutateable) getBankAccount(source);
        if (source_account == null)
            throw new NumberDoesntExistException("account " + source
                    + " unknown at " + name);

        IMutateable dest_account = (IMutateable) getBankAccount(destination);
//        if (dest_account == null)
//            throw new NumberDoesntExistException("account " + destination
//                    + " unknown at " + name);


        System.out.println(source_account.getPrefix());
        // Internal transfer (equal prefix)
        if (dest_account != null && source_account.getPrefix().equals(dest_account.getPrefix())) {
            return internalTransfer(money, source_account, dest_account);
        }

        // Central bank transfer (unequal prefix)
        try {
            return centralBank.transfer(source, destination, money);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean internalTransfer(Money money, IMutateable source_account, IMutateable dest_account) {
        synchronized (accounts) {

            Money negative = Money.difference(new Money(0, money.getCurrency()),
                    money);

            boolean success = source_account.mutate(negative);
            if (!success) return false;

            success = dest_account.mutate(money);

            if (!success) {
                source_account.mutate(money);
            } else {
                BankServer.balancePublisher.informBalance(source_account);
                BankServer.balancePublisher.informBalance(dest_account);
            }
            return success;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean mutate(String nr, Money money) throws RemoteException {
        IMutateable account = (IMutateable) getBankAccount(nr);
        boolean succes = account.mutate(money);
        if (succes) BankServer.balancePublisher.informBalance(account);
        return succes;
    }

    @Override
    public void close() throws Exception {
        UnicastRemoteObject.unexportObject(this, true);
    }
}
