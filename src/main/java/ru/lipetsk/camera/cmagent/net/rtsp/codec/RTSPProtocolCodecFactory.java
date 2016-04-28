package ru.lipetsk.camera.cmagent.net.rtsp.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by Ivan on 15.03.2016.
 */
public class RTSPProtocolCodecFactory implements ProtocolCodecFactory {
    private ProtocolEncoder protocolEncoder;

    private ProtocolDecoder protocolDecoder;

    public RTSPProtocolCodecFactory() {
        this.protocolEncoder = new RequestEncoder();

        this.protocolDecoder = new ResponseDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return this.protocolEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return this.protocolDecoder;
    }
}
