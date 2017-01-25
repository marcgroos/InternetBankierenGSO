package bank.interfaces.communication;


public interface ILoginAccount {
    String getNaam();

    String getReknr();

    boolean checkWachtwoord(String wachtwoord);
}

