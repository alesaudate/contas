package br.com.alesaudate.contas.interfaces.outcoming;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.Period;
import java.util.List;

public abstract class Layout {




    public abstract byte[] formatPeriod(Period period) ;



}
