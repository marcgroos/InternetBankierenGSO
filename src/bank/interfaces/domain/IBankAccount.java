package bank.interfaces.domain;


import bank.server.domain.Money;

import java.io.Serializable;

public interface IBankAccount extends Serializable {
    int getNr();

    Money getBalance();

    IUserAccount getOwner();

    int getCreditLimitInCents();
}

