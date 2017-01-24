package bank.interfaces.domain;

import bank.server.domain.Money;

/**
 * Created by Marc on 24-1-2017.
 */
public interface IMutateable extends IBankAccount {
    /**
     * het saldo van deze bankrekening wordt met bedrag aangepast, tenzij het
     * saldotekort groter wordt dan het maximale krediet, dan verandert er niets
     *
     * @param bedrag is ongelijk aan 0
     * @return (saldo + bedrag) >= -(maximaal krediet)
     */
    boolean mutate(Money bedrag);
}