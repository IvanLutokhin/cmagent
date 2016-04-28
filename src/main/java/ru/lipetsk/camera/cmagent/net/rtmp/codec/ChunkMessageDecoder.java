package ru.lipetsk.camera.cmagent.net.rtmp.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import ru.lipetsk.camera.cmagent.net.common.ConcurrentProtocolDecoder;
import ru.lipetsk.camera.cmagent.net.rtmp.message.ChunkSize;
import ru.lipetsk.camera.cmagent.net.rtmp.message.Header;
import ru.lipetsk.camera.cmagent.net.rtmp.message.MessageType;
import ru.lipetsk.camera.cmagent.net.rtmp.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ivan on 16.02.2016.
 */
public class ChunkMessageDecoder extends ConcurrentProtocolDecoder {
    private Map<Integer, Header> headerMap;

    private int lastPacketSize;

    public ChunkMessageDecoder() {
        this.headerMap = new ConcurrentHashMap<>();

        this.lastPacketSize = 0;
    }

    @Override
    protected Object decodeObject(IoSession ioSession, IoBuffer ioBuffer) throws Exception {
        int chunkSize = (int) ioSession.getAttribute("rtmp.chunk_size.read");

        int remaining = ioBuffer.remaining();

        if (remaining < 1) {
            this.protocolState.buffer(1);

            return null;
        }

        int position = ioBuffer.position();

        int headerSize = 1;

        int headerValue;

        byte b = ioBuffer.get();

        int format = (b >> 6) & 0x03;

        int chunkStreamId = b & 0x3f;

        if (chunkStreamId == 0) {
            if (remaining < 2) {
                this.protocolState.buffer(2);

                ioBuffer.position(position);

                return null;
            }

            headerSize = 2;

            headerValue = (b << 8) | ioBuffer.get();

            chunkStreamId = 64 + headerValue;
        } else if (chunkStreamId == 1) {
            if (remaining < 3) {
                this.protocolState.buffer(3);

                ioBuffer.position(position);

                return null;
            }

            headerSize = 3;

            headerValue = (b << 16) | (ioBuffer.get() << 8) | ioBuffer.get();

            chunkStreamId = (64 + (headerValue >> 8) + ((headerValue) << 8));
        }

        int headerLength;

        switch (format) {
            case 0: headerLength = 11; break;
            case 1: headerLength = 7; break;
            case 2: headerLength = 3; break;
            case 3: headerLength = 0; break;
            default: throw new IllegalArgumentException("Illegal header format");
        }

        headerLength += headerSize;

        if (remaining < headerLength) {
            this.protocolState.buffer(headerLength);

            ioBuffer.position(position);

            return null;
        }

        Header lastHeader = this.headerMap.get(chunkStreamId);

        if ((format != 0) && (lastHeader == null)) {
            throw new IllegalArgumentException("Illegal header");
        }

        Header header = new Header(chunkStreamId);

        int timestamp;

        switch (format) {
            case 0:
                timestamp = ioBuffer.getMediumInt();

                header.setMessageLength(ioBuffer.getMediumInt());

                header.setMessageTypeId(ioBuffer.get());

                header.setMessageStreamId(ioBuffer.getInt());

                if (timestamp == 16777215) {
                    timestamp = ioBuffer.getInt();
                }

                header.setTimestamp(timestamp);

                break;
            case 1:
                timestamp = ioBuffer.getMediumInt();

                header.setTimestamp(lastHeader.getTimestamp());

                header.setMessageLength(ioBuffer.getMediumInt());

                header.setMessageTypeId(ioBuffer.get());

                header.setMessageStreamId(lastHeader.getMessageStreamId());

                if (timestamp == 16777215) {
                    timestamp = ioBuffer.getInt();
                }

                header.setTimestampDelta(timestamp);

                break;
            case 2:
                timestamp = ioBuffer.getMediumInt();

                header.setTimestamp(lastHeader.getTimestamp());

                header.setMessageLength(lastHeader.getMessageLength());

                header.setMessageTypeId(lastHeader.getMessageTypeId());

                header.setMessageStreamId(lastHeader.getMessageStreamId());

                if (timestamp == 16777215) {
                    timestamp = ioBuffer.getInt();
                }

                header.setTimestampDelta(timestamp);

                break;
            case 3:
                header.setTimestamp(lastHeader.getTimestamp());

                header.setMessageLength(lastHeader.getMessageLength());

                header.setMessageTypeId(lastHeader.getMessageTypeId());

                header.setMessageStreamId(lastHeader.getMessageStreamId());

                header.setTimestampDelta(lastHeader.getTimestampDelta());

                break;
            default: throw new IllegalArgumentException("Illegal format in header");
        }

        this.headerMap.put(chunkStreamId, header);

        int readRemaining = header.getMessageLength() - this.lastPacketSize;

        int payloadLength = readRemaining > chunkSize ? chunkSize : readRemaining;

        this.lastPacketSize += payloadLength;

        if (ioBuffer.remaining() < payloadLength) {
            this.protocolState.buffer(headerLength + payloadLength);

            ioBuffer.position(position);

            return null;
        }

        if (header.getMessageLength() == this.lastPacketSize) {
            this.lastPacketSize = 0;
        }

        byte[] payload = new byte[payloadLength];

        ioBuffer.get(payload);

        Message message = MessageType.decode(header, IoBuffer.wrap(payload));

        if (message.getMessageType() == MessageType.CHUNK_SIZE) {
            ioSession.setAttribute("rtmp.chunk_size.read", ((ChunkSize) message).getValue());
        }

        return message;
    }
}