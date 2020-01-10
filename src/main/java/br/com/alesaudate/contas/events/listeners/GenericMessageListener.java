package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.events.EventsProducerService;
import br.com.alesaudate.contas.events.SystemLock;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.bus.Event;
import reactor.fn.Consumer;


@NoArgsConstructor
public abstract class GenericMessageListener <T> implements Consumer<Event<T>> {


    public GenericMessageListener(boolean skipLocks) {
        this.skipLocks = skipLocks;
    }

    private boolean skipLocks = false;

    @Autowired
    private SystemLock lock;


    @Autowired
    @Getter
    private EventsProducerService eventsProducerService;

    @Override
    public void accept(Event<T> tEvent) {
        //TODO tirar os locks daqui e colocar efetivamente somente no final do processo. Pode colocar releaseLock quando chamar para mais comandos
        if (!skipLocks) {
            try {
                lock();
            } catch (InterruptedException e) {
                tEvent.consumeError(e);
                releaseLock();
                return;
            }
        }

        try {
            doAccept(tEvent.getData());
        }
        catch (Exception e) {
            e.printStackTrace();
            tEvent.consumeError(e);
        }
        finally {
            releaseLock();
        }

    }

    protected abstract void doAccept(T data) throws Exception;


    protected void lock() throws InterruptedException {
        lock.acquireLock();
    }


    protected void releaseLock() {
        lock.returnLock();
    }



}
