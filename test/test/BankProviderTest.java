package test;

import bank.centrale.BankCentrale;
import bank.interfaces.communication.ISession;
import bank.server.balie.BankProvider;
import bank.domain.Bank;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author guill
 **/
public class BankProviderTest {

    private static BankProvider balie;

    @Before
    public void setUp() throws Exception {
        balie = new BankProvider(new Bank(new BankCentrale(), "Rabobank"));
    }

    /**
     * @throws Exception
     *
     * This method will return either null or the accountname that
     * it gets when calling it, so were testing when it returns null
     * and when it returns the accountname.
     */
    @Test
    public void openBankAccount() throws Exception {

        // Fill everything "correct" in
        Assert.assertNotNull(balie.openBankAccount("Bretels", "Marcoes", "123456"));

        // Dont send the name
        Assert.assertNull(balie.openBankAccount("", "Maatstad", "Werken"));

        // Dont send the City
        Assert.assertNull(balie.openBankAccount("Bretels", "", "Werken"));

        // Dont fill in the password
        Assert.assertNull(balie.openBankAccount("Bretels", "Maatstad", ""));

        // Send a password that is to short and to long
        Assert.assertNull(balie.openBankAccount("Bretels", "Maat", "wer"));
        Assert.assertNull(balie.openBankAccount("Bretels", "Maat", "werkenisleuk"));
    }

    /**
     * @throws Exception
     *
     * This method will either return null or
     * will return a IBankiersessie, so we check
     * when it gives us what we want and when not.
     */
    @Test
    public void logIn() throws Exception {

        // Send null as accountname and password
        Assert.assertNull(balie.logIn(null, "werk"));
        Assert.assertNull(balie.logIn("Maat", null));

        // Add a account to the balie
        String account = balie.openBankAccount("Arie", "Weert", "Maatwerk");

        // Login with wrong password
        Assert.assertNull(balie.logIn(account, "Maat"));

        // Login with correct password and retreive IBankiersessie
        ISession sessie = balie.logIn(account, "Maatwerk");

        // Check if we got a sessie
        Assert.assertNotNull(sessie);

        // Check if seesie is valid
        Assert.assertTrue(sessie.timeLimitExceeded());
        Assert.assertEquals("Arie", sessie.getRekening().getOwner().getName());
    }

}