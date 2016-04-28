package ru.lipetsk.camera.cmagent.net.rtsp.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import ru.lipetsk.camera.cmagent.net.rtsp.message.Request;
import ru.lipetsk.camera.cmagent.net.common.ConcurrentProtocolEncoder;

/**
 * Created by Ivan on 15.03.2016.
 */
public class RequestEncoder extends ConcurrentProtocolEncoder {
    @Override
    protected IoBuffer encodeObject(IoSession ioSession, Object object) throws Exception {
        return IoBuffer.wrap(((Request) object).getBytes());
    }
}