package test;

import bank.exceptions.NumberDoesntExistException;
import bank.interfaces.domain.IBank;
import bank.server.domain.Bank;
import bank.server.domain.Money;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author guill
 */
public class BankTest {

    private IBank bank1;
    private IBank bank2;
    private Money money = new Money(1000, Money.EURO);
    private Money negative_money = new Money(-100, Money.EURO);

    @Before
    public void setUp() throws Exception {
        bank1 = new Bank("Rabobank");
        bank2 = new Bank("ING-Bank");
    }

    /**
     * @throws Exception
     *
     * First we will open a few Bankaccounts. we will check if they were
     * created on the way they have to be created. We also look if the numbers of these
     * account will be created on a good way.
     */
    @Test
    public void openBankAccount() throws Exception {

        // Open 2 accounts in bank1
        int result = bank1.openBankAccount("Frans Duits", "Frankrijk/Duitsland");
        bank1.openBankAccount("Frans Bauer", "Noord/Brabant");

        // Gaat fout omdat en geen plaats is
        Assert.assertEquals(-1, bank1.openBankAccount("Huisbaas", ""));

        // Open 2 accounts in bank 2
        bank2.openBankAccount("Bob Ross", "America");
        int result2 = bank2.openBankAccount("Rob Geus", "Smaakpolitie");

        // Gaat fout omdat er geen naam is
        Assert.assertEquals(-1 , bank2.openBankAccount("", "Bretels en Marcoes"));

        // Check if accountnumbers are correct
        Assert.assertEquals(100000000, result);
        Assert.assertEquals(100000001, result2);
    }

    /**
     * @throws Exception
     *
     * Add an account to a bank and check if the accountnumber is the same
     * as we the returnvalue from creating it.
     */
    @Test
    public void getBankAccount() throws Exception {

        // Create account on bank 1
        int nr = bank1.openBankAccount("Bob Ross", "America");
        Assert.assertEquals(nr, bank1.getBankAccount(nr).getNr());
        Assert.assertEquals("Bob Ross", bank1.getBankAccount(nr).getOwner().getName());

        // Create account on bank 2
        int nr2 = bank2.openBankAccount("Bretels", "Marcoes");
        Assert.assertEquals(nr2, bank2.getBankAccount(nr2).getNr());
        Assert.assertEquals("Bretels", bank2.getBankAccount(nr2).getOwner().getName());
    }

    /**
     * @throws Exception
     *
     * We will make 2 bankaccounts and test some cases between them.
     * We first transfer money from account 1 to account 2.
     * Then we transfer money to the same account. Next we try to send
     * negative money and at last we try to send money to an not existing account
     */
    @Test
    public void transferMoney() throws Exception {

        // Add 2 accounts to the bank
        bank1.openBankAccount("Ruurd", "Eindhoven");
        bank1.openBankAccount("Ben", "Eindhoven");

        // Check if it returns true when transfer it to other account
        Assert.assertTrue(bank1.transferMoney(100000000, 100000001, money));

        // Try to transfer money to own account
        try{
            bank1.transferMoney(100000000, 100000000, money);
            Assert.fail();

        } catch (RuntimeException ex) {
            System.out.println("Cant send money to the same account");
        }


        // Try to transfer a negative value
        try{
            bank1.transferMoney(100000000, 100000000, negative_money);
            Assert.fail();

        } catch (RuntimeException ex) {
            System.out.println("Can't send negatie money");
        }

        // Try to catch the NumberDoesntExistException
        try{
            bank1.transferMoney(100000000, 69, money);
            Assert.fail();

        } catch (NumberDoesntExistException ex) {
            System.out.println("Account doesnt exist");
        }
    }

    /**
     * @throws Exception
     *
     * We've created 2 bank, we can check if those names are correct by testing
     * the getName() method.
     */
    @Test
    public void getName() throws Exception {

        // Check the names from both banks
        Assert.assertEquals("Rabobank", bank1.getName());
        Assert.assertEquals("ING-Bank", bank2.getName());

    }

}