package ru.lipetsk.camera.cmagent.media.hls;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.media.mpeg2ts.MPEG2TS;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Ivan on 25.03.2016.
 */
public class Fragment {
    private final static Logger logger = LoggerFactory.getLogger(Fragment.class);

    private final String directory;

    private final String enterpriseName;

    private final int index;

    private final long createdAt;

    private long duration;

    private volatile RandomAccessFile randomAccessFile;

    private volatile FileChannel fileChannel;

    private boolean closed;

    private byte continuityCounter;

    private ByteArrayOutputStream byteArrayOutputStream;

    public Fragment(String directory, String enterpriseName, int index) {
        this.directory = directory;

        this.enterpriseName = enterpriseName;

        this.index = index;

        this.createdAt = System.currentTimeMillis();

        this.duration = 0;

        try {
            this.randomAccessFile = new RandomAccessFile(String.format("%s/%s-%s.ts", directory, enterpriseName, index), "rwd");

            this.fileChannel = this.randomAccessFile.getChannel();

            this.fileChannel.write(ByteBuffer.wrap(MPEG2TS.getProgramAssociationTable()));

            this.fileChannel.write(ByteBuffer.wrap(MPEG2TS.getProgramMapTable()));

            this.fileChannel.force(true);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        this.continuityCounter = 0;

        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public String getDirectory() {
        return this.directory;
    }

    public String getEnterpriseName() {
        return this.enterpriseName;
    }

    public String getName() { return String.format("%s-%s.ts", this.enterpriseName, this.index); }

    public int getIndex() {
        return this.index;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public int write(byte[] data) {
        int bytes = 0;

        if (!this.closed && data != null) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.wrap(this.writeAccessUnit(data, this.duration));

                this.duration += 40000;

                bytes = this.fileChannel.write(byteBuffer);

                this.fileChannel.force(true);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return bytes;
    }

    public boolean close() {
        this.closed = true;

        boolean result = false;

        if (this.fileChannel != null && this.fileChannel.isOpen()) {
            try {
                this.fileChannel.close();

                result = true;
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

        return result;
    }

    public void dispose() {
        File file = new File(String.format("%s/%s-%s.ts", this.directory, this.enterpriseName, this.index));

        if (file.exists()) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
    }

    private byte[] writeAccessUnit(byte[] data, long timestamp) {
        int pts = (int) (timestamp * 9L / 100L);

        long pcr = (timestamp - 500000) * 9L / 100L;

        pcr = pcr << 9L;

        IoBuffer ioBuffer = IoBuffer.allocate(MPEG2TS.PACKET_SIZE);

        for (int i = 0; i < MPEG2TS.PACKET_SIZE; i++) {
            ioBuffer.put((byte) 0xff);
        }

        ioBuffer.position(0);

        ioBuffer.put(MPEG2TS.SYNC); // sync byte

        ioBuffer.putShort((short) 0x4100); // Transport Error Indicator b0 | Payload Unit Start Indicator b1 | Transport Priority b0 | PID b0000100000000

        ioBuffer.put((byte) (0x30 | this.getContinuityCounter())); // Scrambling control b00 | Adaptation field flag b1 | Payload flag b1 | Continuity counter b????

        // Adaptation field

        boolean isLastAccessUnit = data.length < 184;

        int paddingSize = isLastAccessUnit ? 184 - data.length : 8;

        ioBuffer.put((byte) (paddingSize - 1));  // Adaptation length

        ioBuffer.put((byte) 0x50); // Random access indicator | PCR flag

        ioBuffer.put((byte) ((pcr >> 34) & 0xff));

        ioBuffer.put((byte) ((pcr >> 26) & 0xff));

        ioBuffer.put((byte) ((pcr >> 18) & 0xff));

        ioBuffer.put((byte) ((pcr >> 10) & 0xff));

        ioBuffer.put((byte) (0x7e | ((pcr & (1 << 9)) >> 2) | ((pcr & (1 << 8)) >> 8)));

        ioBuffer.put((byte) (pcr & 0xff));

        if (isLastAccessUnit) {
            ioBuffer.skip(paddingSize - 8);
        }

        // payload (PES header)

        ioBuffer.putMediumInt(0x000001); // Packet start code prefix

        ioBuffer.put((byte) 0xe0); // Stream id (hardcode video)

        ioBuffer.putShort((short) 0x0000); // PES Packet length

        ioBuffer.put((byte) 0x80); // Marker bits b10 | Scrambling control b00 | Priority b0 | Data alignment indicator b0 | Copyright b0 | Original or Copy b0

        ioBuffer.put((byte) 0x80); // PTS DTS indicator b10 | ESCR flag b0 | ES rate flag b0 | DSM trick mode flag b0 | Additional copy info flag b0 | CRC flag b0 | extension flag b0

        ioBuffer.put((byte) 0x05); // PES header length

        ioBuffer.put((byte) (0x20 | (((pts >> 30) & 7) << 1) | 1));

        ioBuffer.put((byte) ((pts >> 22) & 0xff));

        ioBuffer.put((byte) ((((pts >> 15) & 0x7f) << 1) | 1));

        ioBuffer.put((byte) ((pts >> 7) & 0xff));

        ioBuffer.put((byte) (((pts & 0x7f) << 1) | 1));

        int availablePayloadLength = MPEG2TS.PACKET_SIZE - ioBuffer.position();

        int payloadLength = data.length;

        if (payloadLength > availablePayloadLength) {
            payloadLength = availablePayloadLength;
        }

        ioBuffer.put(data, 0, payloadLength);

        ioBuffer.flip();

        this.output(ioBuffer.array(), true);

        // Next packet

        int offset = payloadLength;

        while (offset < data.length) {
            isLastAccessUnit = (data.length - offset) < 184;

            paddingSize = isLastAccessUnit ? 184 - (data.length - offset) : 8;

            for (int i = 0; i < MPEG2TS.PACKET_SIZE; i++) {
                ioBuffer.put((byte) 0xff);
            }

            ioBuffer.position(0);

            ioBuffer.put(MPEG2TS.SYNC); // sync byte

            ioBuffer.putShort((short) 0x0100); // Transport Error Indicator b0 | Payload Unit Start Indicator b0 | Transport Priority b0 | PID b0000100000000

            ioBuffer.put((byte) (0x30 | this.getContinuityCounter())); // Scrambling control b00 | Adaptation field flag b1 | Payload flag b1 | Continuity counter b????

            // Adaptation field

            ioBuffer.put((byte) (paddingSize - 1));  // Adaptation length

            ioBuffer.put((byte) 0x50); // Random access indicator | PCR flag

            ioBuffer.put((byte) ((pcr >> 34) & 0xff));

            ioBuffer.put((byte) ((pcr >> 26) & 0xff));

            ioBuffer.put((byte) ((pcr >> 18) & 0xff));

            ioBuffer.put((byte) ((pcr >> 10) & 0xff));

            ioBuffer.put((byte) (0x7e | ((pcr & (1 << 9)) >> 2) | ((pcr & (1 << 8)) >> 8)));

            ioBuffer.put((byte) (pcr & 0xff));

            if (isLastAccessUnit) {
                ioBuffer.skip(paddingSize - 8);
            }

            availablePayloadLength = 188 - ioBuffer.position();

            payloadLength = data.length - offset;

            if (payloadLength > availablePayloadLength) {
                payloadLength = availablePayloadLength;
            }

            ioBuffer.put(data, offset, payloadLength);

            offset += payloadLength;

            ioBuffer.flip();

            this.output(ioBuffer.array(), false);
        }

        return this.byteArrayOutputStream.toByteArray();
    }

    private byte getContinuityCounter() {
        if (++this.continuityCounter == 16) {
            this.continuityCounter = 0;
        }

        return this.continuityCounter;
    }

    private void output(byte[] data, boolean reset) {
        if (reset) {
            this.byteArrayOutputStream.reset();
        }

        try {
            this.byteArrayOutputStream.write(data);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}