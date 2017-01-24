package bank.server.domain;

import bank.interfaces.domain.IUserAccount;

class UserAccount implements IUserAccount {

    /**
     *
     */
    private static final long serialVersionUID = -6216851042931199453L;

    private String naam;

    private String plaats;

    public UserAccount(String naam, String plaats) {
        this.naam = naam;
        this.plaats = plaats;
    }

    public String getName() {
        return naam;
    }

    public String getCity() {
        return plaats;
    }

    public int compareTo(IUserAccount arg0) {
        IUserAccount klant = (IUserAccount) arg0;
        int comp = naam.compareTo(klant.getName());
        if (comp != 0) return comp;
        return plaats.compareTo(klant.getCity());
    }

    public boolean equals(IUserAccount o) {
        return this.compareTo(o) == 0;
    }

    public String toString() {
        return naam + " te " + "plaats";
    }

}
