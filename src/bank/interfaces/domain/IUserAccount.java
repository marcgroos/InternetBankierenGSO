package bank.interfaces.domain;

import java.io.Serializable;

public interface IUserAccount extends Serializable, Comparable<IUserAccount> {
    String getName();
    String getCity();
}

