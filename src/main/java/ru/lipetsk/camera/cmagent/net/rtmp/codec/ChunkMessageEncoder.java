package ru.lipetsk.camera.cmagent.net.rtmp.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import ru.lipetsk.camera.cmagent.net.common.ConcurrentProtocolEncoder;
import ru.lipetsk.camera.cmagent.net.rtmp.message.ChunkSize;
import ru.lipetsk.camera.cmagent.net.rtmp.message.Header;
import ru.lipetsk.camera.cmagent.net.rtmp.message.Message;
import ru.lipetsk.camera.cmagent.net.rtmp.message.MessageType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ivan on 17.02.2016.
 */
public class ChunkMessageEncoder extends ConcurrentProtocolEncoder {
    private Map<Integer, Header> headerMap;

    public ChunkMessageEncoder() {
        this.headerMap = new ConcurrentHashMap<>();
    }

    @Override
    protected IoBuffer encodeObject(IoSession ioSession, Object object) throws Exception {
        Message message = (Message) object;

        if (message.getMessageType() == MessageType.CHUNK_SIZE) {
            ioSession.setAttribute("rtmp.chunk_size.write", ((ChunkSize) message).getValue());
        }

        IoBuffer messageBuffer = message.encode();

        if (messageBuffer.position() != 0) {
            messageBuffer.flip();
        } else {
            messageBuffer.rewind();
        }

        int messageLength = messageBuffer.limit();

        int chunkSize = (int) ioSession.getAttribute("rtmp.chunk_size.write");

        int chunkCount = (int) Math.ceil(messageLength / chunkSize);

        Header header = message.getHeader();

        final int chunkStreamId = header.getChunkStreamId();

        Header lastHeader = this.headerMap.get(chunkStreamId);

        this.headerMap.put(chunkStreamId, header);

        int headerFormat = 0;

        int timestamp = 0;

        // get header format
        if (lastHeader != null &&  header.getMessageStreamId() == lastHeader.getMessageStreamId()) {
            headerFormat++;

            if (header.getMessageTypeId() == lastHeader.getMessageTypeId() && messageLength == lastHeader.getMessageLength()) {
                headerFormat++;

                if (header.getTimestamp() == lastHeader.getTimestamp()) {
                    headerFormat++;
                }
            }

            timestamp = header.getTimestamp() - lastHeader.getTimestamp();
        } else {
            timestamp = header.getTimestamp();
        }

        int extendedTimestamp = 0;

        if (timestamp >= 0x00ffffff) {
            extendedTimestamp = timestamp;

            timestamp = 0x00ffffff;
        }

        IoBuffer ioBuffer = IoBuffer.allocate(1);

        ioBuffer.setAutoExpand(true);

        // Encode basic header
        ioBuffer.put(this.encodeBasicHeader(headerFormat, chunkStreamId));

        // Encode message header
        if (headerFormat <= 2) {
            ioBuffer.putMediumInt(timestamp);

            if (headerFormat <= 1) {
                ioBuffer.putMediumInt(messageLength);

                ioBuffer.put((byte) header.getMessageTypeId());

                if (headerFormat == 0) {
                    ioBuffer.put(new byte[] { (byte)(header.getMessageStreamId()), (byte)(header.getMessageStreamId() >>> 8), (byte)(header.getMessageStreamId() >>> 16), (byte) (header.getMessageStreamId() >>> 24)});
                }
            }
        }

        if (extendedTimestamp != 0) {
            ioBuffer.putInt(extendedTimestamp);
        }

        // message chunking
        for (int i = 0; i < chunkCount; i++) {
            ioBuffer.put(messageBuffer.array(), i * chunkSize, chunkSize);

            messageBuffer.skip(chunkSize);

            if (messageBuffer.remaining() > 0) {
                ioBuffer.put(this.encodeBasicHeader(3, chunkStreamId));
            }
        }

        int remaining = messageBuffer.remaining();

        if (remaining > 0) {
            ioBuffer.put(messageBuffer.array(), chunkCount * chunkSize, remaining);
        }

        messageBuffer.free();

        ioBuffer.flip();

        return ioBuffer;
    }

    private byte[] encodeBasicHeader(int format, int chunkStreamId) {
        byte[] basicHeader;

        if (chunkStreamId < 64) {
            basicHeader = new byte[] {(byte) ((format << 6) | (chunkStreamId & 0x3f))};
        } else if (chunkStreamId >= 64 && chunkStreamId < 320) {
            basicHeader = new byte[] {(byte) (format << 6), (byte) (chunkStreamId - 64)};
        } else {
            basicHeader = new byte[] {(byte) (format << 6), (byte) ((chunkStreamId - 64) & 0xff), (byte) ((chunkStreamId) - 64 >> 8)};
        }

        return basicHeader;
    }
}