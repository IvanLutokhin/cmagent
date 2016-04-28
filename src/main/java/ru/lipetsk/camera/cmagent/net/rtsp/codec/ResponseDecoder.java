package ru.lipetsk.camera.cmagent.net.rtsp.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.Publisher;
import ru.lipetsk.camera.cmagent.net.common.ConcurrentProtocolDecoder;
import ru.lipetsk.camera.cmagent.net.common.IPacket;
import ru.lipetsk.camera.cmagent.net.common.InterleavedFrame;
import ru.lipetsk.camera.cmagent.net.rtcp.RTCPPacket;
import ru.lipetsk.camera.cmagent.net.rtp.RTPPacket;
import ru.lipetsk.camera.cmagent.net.rtsp.RTSP;
import ru.lipetsk.camera.cmagent.net.rtsp.message.Header;
import ru.lipetsk.camera.cmagent.net.rtsp.message.IMessage;
import ru.lipetsk.camera.cmagent.net.rtsp.message.Request;
import ru.lipetsk.camera.cmagent.net.rtsp.message.Response;
import ru.lipetsk.camera.cmagent.util.ByteUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Ivan on 15.03.2016.
 */
public class ResponseDecoder extends ConcurrentProtocolDecoder {
    private final static Logger logger = LoggerFactory.getLogger(ResponseDecoder.class);

    protected Object decodeObject(IoSession ioSession, IoBuffer ioBuffer) throws IOException {
        int remaining = ioBuffer.remaining();

        if (remaining < 4) {
            this.protocolState.buffer(4);

            return null;
        }

        InterleavedFrame interleavedFrame = new InterleavedFrame(IoBuffer.wrap(ioBuffer.array(), ioBuffer.position(), 4));

        if (interleavedFrame.validate()) {
            if (remaining < interleavedFrame.getLength() + 4) {
                this.protocolState.buffer(interleavedFrame.getLength() + 4);

                return null;
            }

            ioBuffer.skip(4);

            IPacket packet = (interleavedFrame.getChannel() % 2 == 0)
                    ? new RTPPacket(IoBuffer.wrap(ioBuffer.array(), ioBuffer.position(), interleavedFrame.getLength()))
                    : new RTCPPacket(IoBuffer.wrap(ioBuffer.array(), ioBuffer.position(), interleavedFrame.getLength()));

            ioBuffer.skip(interleavedFrame.getLength());

            return packet;
        }

        if (ByteUtils.startsWith(ioBuffer.array(), RTSP.TOKEN_VERSION.getBytes())) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ioBuffer.asInputStream()))) {
                String line = bufferedReader.readLine();

                //IMessage message = line.startsWith(RTSP.TOKEN_VERSION) ? new Response(line) : new Request(line);
                IMessage message = new Response(line);

                while (true) {
                    line = bufferedReader.readLine();

                    if (line == null || line.isEmpty()) {
                        break;
                    }

                    message.addHeader(new Header(line));
                }

                if (message.containHeader("Content-Length")) {
                    StringBuilder stringBuilder = new StringBuilder();

                    while (true) {
                        line = bufferedReader.readLine();

                        if (line == null || line.isEmpty()) {
                            break;
                        }

                        stringBuilder.append(line).append(RTSP.CRLF);
                    }

                    message.setBody(stringBuilder.toString().getBytes());
                }

                return message;
            }
        }
/*
        int rtpPosition = ioBuffer.indexOf((byte) 0x24);

        int rtspPosition = new String(ioBuffer.array()).indexOf(RTSP.TOKEN_VERSION);

        int position;

        if (rtpPosition != -1 && (rtspPosition == -1 || rtpPosition < rtspPosition)) {
            position = rtpPosition;
        } else if (rtspPosition != -1 && (rtpPosition == -1 || rtpPosition > rtspPosition)) {
            position = rtspPosition;
        } else {
            position = ioBuffer.limit() - 1;
        }

        ioBuffer.position(position);
*/
        ioBuffer.skip(1);

        logger.warn("Could not to decode data");

        return null;
    }
}