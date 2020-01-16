package br.com.alesaudate.contas.events;

import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
public class SystemLock {

    private Semaphore semaphore = new Semaphore(1, true);

    public void acquireLock() throws InterruptedException {
        semaphore.acquire();
    }



    public synchronized void returnLock() {
        semaphore.release();
    }


}
