package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 17.02.2016.
 */
public enum MessageType {
    CHUNK_SIZE(1),
    ABORT(2),
    ACKNOWLEDGEMENT(3),
    CONTROL(4),
    WINDOW_ACKNOWLEDGEMENT_SIZE(5),
    SET_PEER_BANDWIDTH(6),
    AUDIO(8),
    VIDEO(9),
    METADATA_AMF3(15),
    SHARED_OBJECT_AMF3(16),
    COMMAND_AMF3(17),
    METADATA_AMF0(18),
    SHARED_OBJECT_AMF0(19),
    COMMAND_AMF0(20),
    AGGREGATE(22);

    public static MessageType valueOf(int value) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getValue() == value) {
                return messageType;
            }
        }

        return null;
    }

    public static Message decode(Header header, IoBuffer ioBuffer) {
        MessageType messageType = valueOf(header.getMessageTypeId());

        if (messageType == null) {
            throw new IllegalArgumentException("Illegal message type");
        }

        switch (messageType) {
            case CHUNK_SIZE:
                return new ChunkSize(header, ioBuffer);
            case ABORT:
                return new Abort(header, ioBuffer);
            case ACKNOWLEDGEMENT:
                return new Acknowledgement(header, ioBuffer);
            case CONTROL:
                return new Control(header, ioBuffer);
            case WINDOW_ACKNOWLEDGEMENT_SIZE:
                return new WindowAcknowledgementSize(header, ioBuffer);
            case SET_PEER_BANDWIDTH:
                return new SetPeerBandwidth(header, ioBuffer);
            /*case AUDIO:
                return new Audio(header, ioBuffer);*/
            case VIDEO:
                return new VideoData(header, ioBuffer);
            case METADATA_AMF0:
                return new MetadataAMF0(header, ioBuffer);
            case COMMAND_AMF0:
                return new CommandAMF0(header, ioBuffer);
            /*case AGGREGATE:
                return new Aggregate(header, ioBuffer);
                */
            default:
                throw new IllegalArgumentException("Unknown message type ID: " + messageType.getValue());
        }
    }

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public int getDefaultChunkStreamId() {
        switch (this) {
            case CHUNK_SIZE:
            case ABORT:
            case ACKNOWLEDGEMENT:
            case CONTROL:
            case WINDOW_ACKNOWLEDGEMENT_SIZE:
            case SET_PEER_BANDWIDTH:
                return 2;
            case COMMAND_AMF0:
            case COMMAND_AMF3:
                return 3;
        }

        return 4;
    }
}