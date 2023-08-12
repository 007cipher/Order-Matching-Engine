package com.cipher.liquidity.provider.websocket;

import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;

@Slf4j
@ClientEndpoint
public class WebsocketClient {

    private Session session = null;
    private MessageHandler handler;

    private String instrument;

    public WebsocketClient(URI endpointURI, String instrument) throws DeploymentException, IOException {
        this.instrument = instrument;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, endpointURI);
    }

    @OnOpen
    public void onOpen(Session session) {
        try {
            this.session = session;
            String msg = "{ \"event\": \"subscribe\", \"channel\": \"book\", \"symbol\": \"t" + instrument + "\"}";
            session.getBasicRemote().sendText(msg);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.handler = msgHandler;
    }

    @OnMessage
    public void processMessage(String message) {
        if (this.handler != null) {
            this.handler.handleMessage(message);
        }
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            log.error("Error: {}", ex);
        }
    }


    public static interface MessageHandler {
        public void handleMessage(String message);
    }
}