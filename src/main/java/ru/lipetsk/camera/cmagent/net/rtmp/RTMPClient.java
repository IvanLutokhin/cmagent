package ru.lipetsk.camera.cmagent.net.rtmp;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.lipetsk.camera.cmagent.event.*;
import ru.lipetsk.camera.cmagent.net.common.BaseConnection;
import ru.lipetsk.camera.cmagent.net.rtmp.io.RTMPIoHandler;
import ru.lipetsk.camera.cmagent.net.rtmp.message.*;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMF0;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMFObject;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Ivan on 15.02.2016.
 */
public class RTMPClient extends EventDispatcher implements IRTMPClient, IEventListener {
    private final static Logger logger = LoggerFactory.getLogger(RTMPClient.class);

    private Map<String, Object> connectionArgs;

    private int streamId;

    private AtomicInteger transactionId;

    private Map<Integer, Command> commandMap;

    private boolean published;

    private BaseConnection baseConnection;

    public RTMPClient() {
        this.transactionId = new AtomicInteger(1);

        this.commandMap = new ConcurrentHashMap<>();

        this.published = false;

        this.baseConnection = new RTMPConnection();

        this.baseConnection.addEventListener(Events.CONNECTION_OPENED, this);

        this.baseConnection.addEventListener(Events.CONNECTION_CLOSED, this);

        this.baseConnection.addEventListener(Events.MESSAGE_RECEIVED, this);

        this.baseConnection.addEventListener(Events.MESSAGE_SENT, this);
    }

    public int getStreamId() {
        return this.streamId;
    }

    @Override
    public boolean connect(String host, int port, Map<String, Object> connectionArgs) {
        this.connectionArgs = connectionArgs;

        if (!this.connectionArgs.containsKey("objectEncoding")) {
            this.connectionArgs.put("objectEncoding", 0);
        }

        return this.baseConnection.connect(host, port);
    }

    @Override
    public boolean connect(String host, int port, String application) {
        Map<String, Object> defaultConnectionArgs = RTMP.getDefaultConnectionArgs(host, port, application);

        return this.connect(host, port, defaultConnectionArgs);
    }

    @Override
    public boolean isConnected() {
        return this.baseConnection.isConnected();
    }

    @Override
    public void disconnect() {
        this.streamId = 0;

        this.transactionId.set(0);

        this.published = false;

        this.baseConnection.disconnect();
    }

    @Override
    public void publishStream(String streamName) {
        if (this.streamId != 0) {
            this.publish(streamName, this.streamId);
        }
    }

    @Override
    public void writeStreamData(Message message) {
        if (this.streamId != 0 && this.published) {
            message.getHeader().setMessageStreamId(this.streamId);

            this.baseConnection.send(message);
        }
    }

    @Override
    public void onEventHandle(IEvent event) {
        switch (event.getName()) {
            case Events.CONNECTION_OPENED:
                this.connect();

                break;
            case Events.CONNECTION_CLOSED:

                break;
            case Events.MESSAGE_RECEIVED:
                this.onMessageReceived((Message) event.getContext());

                break;
            case Events.MESSAGE_SENT:

                break;
        }
    }

    private void onMessageReceived(Message message) {
        switch (message.getMessageType()) {
            case CHUNK_SIZE:
                logger.debug("on ChunkSize");

                ChunkSize chunkSize = (ChunkSize) message;

                this.baseConnection.send(chunkSize);

                break;
            case ACKNOWLEDGEMENT:
                logger.debug("on Acknowledgement");

                break;
            case WINDOW_ACKNOWLEDGEMENT_SIZE:
                logger.debug("on WindowAcknowledgementSize");

                WindowAcknowledgementSize windowAcknowledgementSize = (WindowAcknowledgementSize) message;

                this.baseConnection.send(windowAcknowledgementSize);

                break;
            case SET_PEER_BANDWIDTH:
                logger.debug("on SetPeerBandwidth");

                break;
            case CONTROL:
                Control control = (Control) message;

                switch (control.getType()) {
                    case STREAM_BEGIN:
                        logger.info("Stream begin {}", this.streamId);

                        break;
                    case STREAM_EOF:
                        logger.info("Stream EOF {}", this.streamId);

                        break;
                    case STREAM_IS_RECORDED:
                        logger.info("Stream is recorded {}", this.streamId);

                        break;
                    case PING_REQUEST:
                        logger.info("Ping request");

                        int timestamp = control.getTimestamp();

                        Control pingResponse = new Control(Control.Type.PING_RESPONSE, timestamp);

                        this.baseConnection.send(pingResponse);

                        this.dispatchEvent(new Event(Events.RTMP_NO_DATA));

                        break;
                    default:
                        logger.warn("Ignore control message {}", control.getType());
                }

                break;
            case COMMAND_AMF0:
                logger.debug("on Command (AMF0)");

                Command response = (CommandAMF0) message;

                if ("_error".equalsIgnoreCase(response.getCommandName())) {
                    logger.error(response.getCommandName());

                    return;
                }

                if ("_result".equals(response.getCommandName())) {
                    Command request = this.commandMap.remove(response.getTransactionId());

                    logger.debug("Result has been received for pending call [{}]", request);

                    this.handleCommand(request, response);

                    return;
                }

                if ("onStatus".equals(response.getCommandName())) {
                    Map<String, Object> map = (Map<String, Object>) response.getUserArgument(0);

                    if ("NetStream.Publish.Start".equals(map.get("code"))) {
                        this.published = true;

                        this.dispatchEvent(new Event(Events.RTMP_PUBLISH_STREAM));

                        return;
                    }
                }

                break;

            default:
                logger.warn("Ignore RTMP Message {}", message.getMessageType().getValue());
        }
    }


    private void handleCommand(Command request, Command response) {
        if ("connect".equals(request.getCommandName())) {
            this.createStream();

            return;
        }

        if ("createStream".equals(request.getCommandName())) {
            this.streamId = ((Double) response.getUserArgument(0)).intValue();

            logger.info("Stream ID: {}", this.streamId);

            this.dispatchEvent(new Event(Events.RTMP_CREATE_STREAM));

            return;
        }

        logger.warn("Unknown command name: " + request.getCommandName());
    }

    private void connect() {
        AMFObject amfObject = AMF0.amfObject(this.connectionArgs);

        Command command = new CommandAMF0("connect", 1, amfObject);

        this.commandMap.put(command.getTransactionId(), command);

        this.baseConnection.send(command);
    }

    private void createStream() {
        Command command = new CommandAMF0("createStream", this.transactionId.incrementAndGet(), null);

        this.commandMap.put(command.getTransactionId(), command);

        this.baseConnection.send(command);
    }

    private void publish(String streamName, int streamId) {
        Command command = new CommandAMF0("publish", null, streamName, "live");

        command.getHeader().setChunkStreamId(4);

        command.getHeader().setMessageStreamId(streamId);

        this.baseConnection.send(command);
    }
}