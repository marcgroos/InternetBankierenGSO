package bank.centrale;

import bank.domain.Money;
import bank.exceptions.NumberDoesntExistException;
import bank.server.rmi.BalancePublisher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guill on 25-1-2017.
 */
public class BankCentrale extends UnicastRemoteObject implements IBankCentrale, AutoCloseable {

    final Map<Integer, String> rekeningen = new HashMap<>();
    private final Map<String, IBankTransfer> banken = new HashMap<>();
    private BalancePublisher balancePublisher;

    public BankCentrale() throws RemoteException{ }

    @Override
    public int getUniqueRekNr(String bankName) throws RemoteException {
        synchronized (rekeningen){
            for (int i = 0; ; i++){
                if(!rekeningen.containsKey(i)){
                    rekeningen.put(i, bankName);
                    return i;
                }
            }
        }
    }

    @Override
    public void registerBank(String banName, IBankTransfer bank) throws RemoteException {
        banken.put(banName, bank);
    }

    @Override
    public boolean transfer(int from, int to, Money amount) throws RemoteException, NumberDoesntExistException {
        IBankTransfer src = getBankForRekening(from);
        IBankTransfer dst = getBankForRekening(to);

        Money negative = Money.difference(new Money(0, amount.getCurrency()), amount);
        boolean success = src.mutate(from, negative);
        if(!success) return false;

        success = dst.mutate(to, amount);

        if(!success){
            src.mutate(from, amount);
        }

        return success;
    }

    IBankTransfer getBankForRekening(int rekNr) throws NumberDoesntExistException{
        String bankName = rekeningen.get(rekNr);
        if(bankName == null) throw  new NumberDoesntExistException("account" + rekNr + "is unknown");
        return banken.get(bankName);
    }

    @Override
    public void close() throws Exception {
        UnicastRemoteObject.unexportObject(this, true);
    }
}
