package bank.server.domain;


import bank.interfaces.domain.IBank;
import bank.interfaces.domain.IKlant;
import bank.interfaces.domain.IRekening;
import bank.interfaces.domain.IRekeningBank;
import fontys.util.NumberDoesntExistException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Bank implements IBank {

    /**
     *
     */
    private static final long serialVersionUID = -8728841131739353765L;
    private Map<Integer, IRekeningBank> accounts;
    private Collection<IKlant> clients;
    private int nieuwReknr;
    private String name;

    public Bank(String name) {
        accounts = new HashMap<Integer, IRekeningBank>();
        clients = new ArrayList<IKlant>();
        nieuwReknr = 100000000;
        this.name = name;
    }

    public int openRekening(String name, String city) {
        if (name.equals("") || city.equals(""))
            return -1;

        IKlant klant = getKlant(name, city);
        IRekeningBank account = new Rekening(nieuwReknr, klant, Money.EURO);
        accounts.put(nieuwReknr, account);
        nieuwReknr++;
        return nieuwReknr - 1;
    }

    private IKlant getKlant(String name, String city) {
        for (IKlant k : clients) {
            if (k.getNaam().equals(name) && k.getPlaats().equals(city))
                return k;
        }
        IKlant klant = new Klant(name, city);
        clients.add(klant);
        return klant;
    }

    public IRekening getRekening(int nr) {
        return accounts.get(nr);
    }

    public boolean maakOver(int source, int destination, Money money)
            throws NumberDoesntExistException {
        if (source == destination)
            throw new RuntimeException(
                    "cannot transfer money to your own account");
        if (!money.isPositive())
            throw new RuntimeException("money must be positive");

        IRekeningBank source_account = (IRekeningBank) getRekening(source);
        if (source_account == null)
            throw new NumberDoesntExistException("account " + source
                    + " unknown at " + name);

        Money negative = Money.difference(new Money(0, money.getCurrency()),
                money);
        boolean success = source_account.muteer(negative);
        if (!success)
            return false;

        IRekeningBank dest_account = (IRekeningBank) getRekening(destination);
        if (dest_account == null)
            throw new NumberDoesntExistException("account " + destination
                    + " unknown at " + name);
        success = dest_account.muteer(money);

        if (!success) // rollback
            source_account.muteer(money);
        return success;
    }

    @Override
    public String getName() {
        return name;
    }

}
