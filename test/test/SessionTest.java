package test;

import bank.domain.CentralBank;
import bank.interfaces.communication.ISession;
import bank.interfaces.domain.IBank;
import bank.server.balie.Session;
import bank.domain.Bank;
import bank.domain.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *@author guill  
 **/
public class SessionTest {

    private IBank bank1;
    private ISession sessie;

    private String rekNr1, rekNr2;

    @Before
    public void setUp() throws Exception {
        bank1 = new Bank(new CentralBank(), "Rabobank");
        rekNr1 = bank1.openBankAccount("Hans", "Maat");
        rekNr2 = bank1.openBankAccount("Piet", "Werk");

        sessie = new Session(rekNr1, bank1);
    }

    /**
     * @throws Exception
     *
     * This method gives as return flase or true.
     * If a specified time ends, it wil turn to false and the
     * connection cant be made, you need to login
     */
    @Test
    public void isGeldig() throws Exception {

        // Check if sessie has the right owner an is valid
        Assert.assertEquals("Hans", sessie.getRekening().getOwner().getName());
        Assert.assertTrue(sessie.timeLimitExceeded());
    }

    /**
     * @throws Exception
     *
     * This method throws an RuntimeException if the accounts are the
     * same or the amount is negative. Otherwise it would would transfer
     * the money and will return a 'true' boolean
     */
    @Test
    public void maakOver() throws Exception {

        Money positief = new Money(1000, Money.EURO);
        Money negatief = new Money(-100, Money.EURO);

        // Try to transfer -1 euro to account 2
        Assert.assertTrue(sessie.transferMoney(rekNr2, positief));

        // Try to transfer 10 euros to account 2
        try {
            sessie.transferMoney(rekNr2, negatief);
            Assert.fail();
        } catch (RuntimeException ex) {
            System.out.println("Cant transfer negative money");
        }

        // Try to send money to yourself
        try {
            sessie.transferMoney(rekNr1, positief);
            Assert.fail();
        } catch (RuntimeException ex) {
            System.out.println("Cant transfer money to yourself");
        }
    }
}