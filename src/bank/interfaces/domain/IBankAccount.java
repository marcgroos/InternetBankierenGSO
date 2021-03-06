package bank.interfaces.domain;


import bank.domain.Money;

import java.io.Serializable;

public interface IBankAccount extends Serializable {
    int getNr();

    Money getBalance();

    IUserAccount getOwner();

    int getCreditLimitInCents();
}

