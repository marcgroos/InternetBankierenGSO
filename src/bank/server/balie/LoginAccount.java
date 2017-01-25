package bank.server.balie;


import bank.interfaces.communication.ILoginAccount;

public class LoginAccount implements ILoginAccount {

    private String naam;
    private String wachtwoord;
    private String reknr;

    public LoginAccount(String naam, String wachtwoord, String rekening) {
        this.naam = naam;
        this.wachtwoord = wachtwoord;
        this.reknr = rekening;
    }

    public boolean checkWachtwoord(String wachtwoord) {
        return this.wachtwoord.equals(wachtwoord);
    }

    public String getNaam() {
        return naam;
    }

    public String getReknr() {
        return reknr;
    }

}
