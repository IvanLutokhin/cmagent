package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.util.CRC32;

/**
 * Created by Ivan on 21.03.2016.
 */
public class MPEG2TS {
    public static final byte SYNC = 0x47;

    public static final int PACKET_SIZE = 188;

    public static byte[] getProgramAssociationTable() {
        IoBuffer ioBuffer = IoBuffer.allocate(PACKET_SIZE);

        for (int i = 0; i < PACKET_SIZE; i++) {
            ioBuffer.put((byte) 0xff);
        }

        ioBuffer.position(0);

        ioBuffer.put(SYNC); // sync byte

        ioBuffer.putShort((short) 0x4000); // Transport Error Indicator b0 | Payload Unit Start Indicator b1 | Transport Priority b0 | PID b0000000000000

        ioBuffer.put((byte) 0x10); // Scrambling control b00 | Adaptation field flag b0 | Payload flag b1 | Continuity counter b0000

        ioBuffer.put((byte) 0x00); // adaptation field

        // payload (table header)

        ioBuffer.put((byte) 0x00); // Table ID

        ioBuffer.putShort((short) 0xb00d); // Section syntax indicator b1 | Private bit b0 | Reserved bits b11 | Section length unused bits b00 | Section length b0000001101

        // payload (table section)

        ioBuffer.putShort((short) 0x0001); // Table ID extension (Transport Stream Identifier)

        ioBuffer.put((byte) 0xc1); // Reserved bits b11 | Version number b00000 | Current/next indicator b1

        ioBuffer.put((byte) 0x00); // Section number

        ioBuffer.put((byte) 0x00); // Last section number

        // payload (data)

        ioBuffer.putShort((short) 0x0001); // Program num

        ioBuffer.putShort((short) 0xf001); // Reserved bits b111 | Program map PID b1000000000001

        byte[] crc32 = CRC32.compute(ioBuffer.array(), 5, 12);

        ioBuffer.put(crc32);

        ioBuffer.flip();

        return ioBuffer.array();
    }

    public static byte[] getProgramMapTable() {
        IoBuffer ioBuffer = IoBuffer.allocate(PACKET_SIZE);

        for (int i = 0; i < PACKET_SIZE; i++) {
            ioBuffer.put((byte) 0xff);
        }

        ioBuffer.position(0);

        ioBuffer.put(SYNC); // sync byte

        ioBuffer.putShort((short) 0x5001); // Transport Error Indicator b0 | Payload Unit Start Indicator b1 | Transport Priority b0 | PID b1000000000001

        ioBuffer.put((byte) 0x10); // Scrambling control b00 | Adaptation field flag b0 | Payload flag b1 | Continuity counter b0000

        ioBuffer.put((byte) 0x00); // adaptation field

        // payload (table header)

        ioBuffer.put((byte) 0x02); // Table ID

        ioBuffer.putShort((short) 0xb012); // Section syntax indicator b1 | Private bit b0 | Reserved bits b11 | Section length unused bits b00 | Section length b??????????

        // payload (table section)

        ioBuffer.putShort((short) 0x0001); // Table ID extension (Program number)

        ioBuffer.put((byte) 0xc1); // Reserved bits b11 | Version number b00000 | Current/next indicator b1

        ioBuffer.put((byte) 0x00); // Section number

        ioBuffer.put((byte) 0x00); // Last section number

        // payload (data)

        ioBuffer.putShort((short) 0xe100); // Reserved bits b111 | Program map PID b0000000000000

        ioBuffer.putShort((short) 0xf000); // Reserved bits b1111 | Program info length unused bits b00 | Program info length b0000000000

        // payload (elementary stream)
        ioBuffer.put((byte) 0x1b); // Stream type (hardcode AVC H264)

        ioBuffer.putShort((short) 0xe100); // Reserved bits b111 | Elementary PID b0000100000000

        ioBuffer.putShort((short) 0xf000); // Reserved bits b1111 | ES Info length unused bits b00 | ES Info length length b0000000000

        /*ioBuffer.put((byte) 0x0f); // Stream type (hardcode AAC)

        ioBuffer.putShort((short) 0xe101); // Reserved bits b111 | Elementary PID b0000000000001

        ioBuffer.putShort((short) 0xf000); // Reserved bits b1111 | ES Info length unused bits b00 | ES Info length length b0000000000*/

        //byte[] crc32 = this.computeCRC32(ioBuffer.array(), 5, 17);

        byte[] crc32 = CRC32.compute(ioBuffer.array(), 5, 17);

        ioBuffer.put(crc32);

        ioBuffer.flip();

        return ioBuffer.array();
    }
}