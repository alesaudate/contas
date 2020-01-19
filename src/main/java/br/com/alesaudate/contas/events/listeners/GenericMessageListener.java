package br.com.alesaudate.contas.events.listeners;

public abstract class GenericMessageListener <T> {

    public abstract void accept(T data) throws Exception;


}
