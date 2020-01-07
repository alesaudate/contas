package br.com.alesaudate.contas.domain;

import java.math.BigDecimal;

public enum EntryType {

    DEBT {
        @Override
        public BigDecimal getNormalizedAmount(BigDecimal amount) {
            return amount.negate();
        }
    }, CREDIT;



    public BigDecimal getNormalizedAmount(BigDecimal amount) {
        return amount;
    }
}
