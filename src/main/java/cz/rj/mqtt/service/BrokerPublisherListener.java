package cz.rj.mqtt.service;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import lombok.extern.slf4j.Slf4j;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class BrokerPublisherListener extends AbstractInterceptHandler {
    @Override
    public String getID() {
        return "EmbeddedLauncherPublishListener";
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        final String decodedPayload = msg.getPayload().toString(UTF_8);
        log.info("received,topic={},content={},user={},clientID={}", msg.getTopicName(), decodedPayload,
                msg.getUsername(), msg.getClientID());

        // super handle the message buffer count
        super.onPublish(msg);
    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage msg) {
        log.info("acknowledged,topic={},{}", msg.getTopic(), msg.getPacketID());
    }

    @Override
    public void onSessionLoopError(Throwable error) {
        log.info("session event loop", error);
    }
}
