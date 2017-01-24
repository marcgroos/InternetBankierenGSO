package bank.interfaces.communication;


public interface ILoginAccount {
    String getNaam();

    int getReknr();

    boolean checkWachtwoord(String wachtwoord);
}

