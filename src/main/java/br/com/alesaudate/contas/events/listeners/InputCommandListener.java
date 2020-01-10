package br.com.alesaudate.contas.events.listeners;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

public abstract class InputCommandListener extends GenericMessageListener<String> {


    @Override
    protected void doAccept(String data) {
        if (matchExpected(data)) {
            process(data);
        }
        else {
            getEventsProducerService().publishReadyForEvents();
        }
    }


    protected abstract boolean matchExpected(String message);

    protected abstract void process(String message);


    protected boolean isMessageClose(String receivedMessage, String expectedMessage) {

        receivedMessage = receivedMessage.trim().toLowerCase();
        receivedMessage = StringUtils.stripAccents(receivedMessage);

        expectedMessage = expectedMessage.trim().toLowerCase();
        expectedMessage = StringUtils.stripAccents(expectedMessage);

        double expectedMessageLength = expectedMessage.length();
        double distance = LevenshteinDistance.getDefaultInstance().apply(receivedMessage, expectedMessage);



        double threshold = similarityLevelThreshold();
        double minimumSize = threshold * expectedMessageLength;

        return  expectedMessageLength - distance > minimumSize;

    }


    protected double similarityLevelThreshold() {
        return 0.9;
    }

}
