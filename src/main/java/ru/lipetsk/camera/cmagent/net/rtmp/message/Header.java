package ru.lipetsk.camera.cmagent.net.rtmp.message;

/**
 * Created by Ivan on 16.02.2016.
 */
public class Header {
    private int chunkStreamId;

    private int timestamp;

    private int timestampDelta;

    private int messageLength;

    private int messageTypeId;

    private int messageStreamId;

    public Header(MessageType messageType) {
        this(messageType.getDefaultChunkStreamId());

        this.messageTypeId = messageType.getValue();
    }

    public Header(int chunkStreamId) {
        this.chunkStreamId = chunkStreamId;
    }

    public int getChunkStreamId() {
        return this.chunkStreamId;
    }

    public void setChunkStreamId(int chunkStreamId) {
        this.chunkStreamId = chunkStreamId;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestampDelta() {
        return this.timestampDelta;
    }

    public void setTimestampDelta(int timestampDelta) {
        this.timestampDelta = timestampDelta;
    }

    public int getMessageLength() {
        return this.messageLength;
    }

    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    public int getMessageTypeId() {
        return this.messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public int getMessageStreamId() {
        return this.messageStreamId;
    }

    public void setMessageStreamId(int messageStreamId) {
        this.messageStreamId = messageStreamId;
    }
}