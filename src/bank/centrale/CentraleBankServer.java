package bank.centrale;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Created by guill on 25-1-2017.
 */
public class CentraleBankServer {
    public static void main(String[] arg){
        try(BankCentrale bankCentrale = new BankCentrale()){

            System.out.println("BankCentrale has started");

            // Bind Bancentrale on registry
            Registry registry = LocateRegistry.createRegistry(12345);
            registry.rebind("bankcentrale", bankCentrale);

            System.out.println("BankCentrale is bound in the registry");

            // Waiting to close
            new Scanner(System.in).next();
            System.out.println("BankCentrale is closing");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
