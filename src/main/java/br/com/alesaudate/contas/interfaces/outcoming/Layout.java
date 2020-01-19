package br.com.alesaudate.contas.interfaces.outcoming;

import br.com.alesaudate.contas.domain.Entry;

import java.io.IOException;
import java.util.List;

public abstract class Layout {



    public abstract byte[] format(List<Entry> entryList) throws IOException;

    public abstract String getExtension();
}
