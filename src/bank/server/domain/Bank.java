package bank.server.domain;


import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.domain.IBank;
import bank.interfaces.domain.IUserAccount;
import bank.interfaces.domain.IBankAccount;
import bank.interfaces.domain.IMutateable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Bank implements IBank {

    /**
     *
     */
    private static final long serialVersionUID = -8728841131739353765L;
    private Map<Integer, IMutateable> accounts;
    private Collection<IUserAccount> clients;
    private int nieuwReknr;
    private String name;

    public Bank(String name) {
        accounts = new HashMap<Integer, IMutateable>();
        clients = new ArrayList<IUserAccount>();
        nieuwReknr = 100000000;
        this.name = name;
    }

    public int openBankAccount(String name, String city) {
        if (name.equals("") || city.equals(""))
            return -1;

        IUserAccount klant = getUserAccount(name, city);
        IMutateable account = new BankAccount(nieuwReknr, klant, Money.EURO);
        accounts.put(nieuwReknr, account);
        nieuwReknr++;
        return nieuwReknr - 1;
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

    public IBankAccount getBankAccount(int nr) {
        return accounts.get(nr);
    }

    public boolean transferMoney(int source, int destination, Money money)
            throws NumberDoesntExistException {
        if (source == destination)
            throw new RuntimeException(
                    "cannot transferMoney money to your own account");
        if (!money.isPositive())
            throw new RuntimeException("money must be positive");

        IMutateable source_account = (IMutateable) getBankAccount(source);
        if (source_account == null)
            throw new NumberDoesntExistException("account " + source
                    + " unknown at " + name);

        Money negative = Money.difference(new Money(0, money.getCurrency()),
                money);
        boolean success = source_account.mutate(negative);
        if (!success)
            return false;

        IMutateable dest_account = (IMutateable) getBankAccount(destination);
        if (dest_account == null)
            throw new NumberDoesntExistException("account " + destination
                    + " unknown at " + name);
        success = dest_account.mutate(money);

        if (!success) // rollback
            source_account.mutate(money);
        return success;
    }

    @Override
    public String getName() {
        return name;
    }

}
