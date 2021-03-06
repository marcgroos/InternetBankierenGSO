package bank.domain;


import bank.interfaces.domain.IUserAccount;
import bank.interfaces.domain.IBankAccount;
import bank.interfaces.domain.IMutateable;
import bank.server.rmi.BalancePublisher;

import java.rmi.RemoteException;

class BankAccount implements IMutateable {

    private static final long serialVersionUID = 7221569686169173632L;
    private static final int CREDIT_LIMIT = -10000;
    private int nr;
    private IUserAccount owner;
    private Money balance;
    private BalancePublisher publisher;

    /**
     * creatie van een bankrekening met balance van 0.0<br>
     * de constructor heeft package-access omdat de PersistentAccount-objecten door een
     * het PersistentBank-object worden beheerd
     *
     * @param number   het bankrekeningnummer
     * @param klant    de owner van deze rekening
     * @param currency de munteenheid waarin het balance is uitgedrukt
     * @see banking.persistence.PersistentBank
     */
    BankAccount(int number, IUserAccount klant, String currency) {
        this(number, klant, new Money(0, currency));
    }

    /**
     * creatie van een bankrekening met balance balance<br>
     * de constructor heeft package-access omdat de PersistentAccount-objecten door een
     * het PersistentBank-object worden beheerd
     *
     * @param number   het bankrekeningnummer
     * @param name     de naam van de owner
     * @param city     de woonplaats van de owner
     * @param currency de munteenheid waarin het balance is uitgedrukt
     * @see banking.persistence.PersistentBank
     */
    BankAccount(int number, IUserAccount klant, Money balance) {
        this.nr = number;
        this.owner = klant;
        this.balance = balance;

        try {
            this.publisher = new BalancePublisher(this.nr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean equals(Object obj) {
        return nr == ((IBankAccount) obj).getNr();
    }

    public int getNr() {
        return nr;
    }

    public String toString() {
        return nr + ": " + owner.toString();
    }

    boolean isTransferPossible(Money bedrag) {
        return (bedrag.getCents() + balance.getCents() >= CREDIT_LIMIT);
    }

    public IUserAccount getOwner() {
        return owner;
    }

    public Money getBalance() {
        return balance;
    }

    public boolean mutate(Money bedrag) {
        if (bedrag.getCents() == 0) {
            throw new RuntimeException(" bedrag = 0 bij aanroep 'mutate'");
        }

        if (isTransferPossible(bedrag)) {
            Money oldBalance = new Money(balance.getCents(), Money.EURO);
            balance = Money.sum(balance, bedrag);

            publisher.informBalance(oldBalance, balance);

            return true;
        }
        return false;
    }

    @Override
    public int getCreditLimitInCents() {
        return CREDIT_LIMIT;
    }
}
