package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 20.02.2016.
 */
public class VideoData extends Message {
    public enum FrameType {
        KEYFRAME(1),
        INTER_FRAME(2),
        DISPOSABLE_INTER_FRAME(3),
        GENERATED_KEYFRAME(4),
        INFO_FRAME(5);

        public static FrameType valueOf(int value) {
            for (FrameType frameType : FrameType.values()) {
                if (frameType.getValue() == value) {
                    return frameType;
                }
            }

            return null;
        }

        private final int value;

        FrameType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum Codec {
        JPEG(1),
        SORENSON_H323(2),
        SCREEN_VIDEO(3),
        ON2_VP6(4),
        ON2_VP6_ALPHA(5),
        SCREEN_VIDEO_2(6),
        AVC(7);

        public static Codec valueOf(int value) {
            for (Codec codec : Codec.values()) {
                if (codec.getValue() == value) {
                    return codec;
                }
            }

            return null;
        }

        private final int value;

        Codec(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    private FrameType frameType;

    private Codec codec;

    private byte[] data;

    public VideoData(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public VideoData(IoBuffer buffer) {
        this.decode(buffer);
    }

    public FrameType getFrameType() {
        return frameType;
    }

    public void setFrameType(FrameType frameType) {
        this.frameType = frameType;
    }

    public Codec getCodec() {
        return codec;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.VIDEO;
    }

    @Override
    public void decode(IoBuffer ioBuffer) {
        byte b = ioBuffer.get();

        int frameType = (b & 0xf0) >> 4;

        int codecId = (b & 0x0f);

        this.frameType = FrameType.valueOf(frameType);

        this.codec = Codec.valueOf(codecId);

        this.data = new byte[ioBuffer.remaining()];

        ioBuffer.get(this.data);
    }

    @Override
    public IoBuffer encode() {
        IoBuffer ioBuffer = IoBuffer.allocate(this.data.length + 1);

        ioBuffer.put((byte) ((this.frameType.getValue() << 4) | this.codec.getValue()));

        ioBuffer.put(this.data);

        ioBuffer.flip();

        return ioBuffer;
    }
}