package test;

import bank.interfaces.communication.IBankierSessie;
import bank.server.balie.Balie;
import bank.server.domain.Bank;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author guill
 **/
public class BalieTest {

    private static Balie balie;

    @Before
    public void setUp() throws Exception {
        balie = new Balie(new Bank("Rabobank"));
    }

    /**
     * @throws Exception
     *
     * This method will return either null or the accountname that
     * it gets when calling it, so were testing when it returns null
     * and when it returns the accountname.
     */
    @Test
    public void openRekening() throws Exception {

        // Fill everything "correct" in
        Assert.assertNotNull(balie.openRekening("Bretels", "Marcoes", "123456"));

        // Dont send the name
        Assert.assertNull(balie.openRekening("", "Maatstad", "Werken"));

        // Dont send the City
        Assert.assertNull(balie.openRekening("Bretels", "", "Werken"));

        // Dont fill in the password
        Assert.assertNull(balie.openRekening("Bretels", "Maatstad", ""));

        // Send a password that is to short and to long
        Assert.assertNull(balie.openRekening("Bretels", "Maat", "wer"));
        Assert.assertNull(balie.openRekening("Bretels", "Maat", "werkenisleuk"));
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
        String account = balie.openRekening("Arie", "Weert", "Maatwerk");

        // Login with wrong password
        Assert.assertNull(balie.logIn(account, "Maat"));

        // Login with correct password and retreive IBankiersessie
        IBankierSessie sessie = balie.logIn(account, "Maatwerk");

        // Check if we got a sessie
        Assert.assertNotNull(sessie);

        // Check if seesie is valid
        Assert.assertTrue(sessie.isGeldig());
        Assert.assertEquals("Arie", sessie.getRekening().getEigenaar());
    }

}