package ru.lipetsk.camera.cmagent.net.rtmp.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by Ivan on 16.02.2016.
 */
public class RTMPProtocolCodecFactory implements ProtocolCodecFactory {
    private ChunkMessageEncoder chunkMessageEncoder;

    private ChunkMessageDecoder chunkMessageDecoder;

    public RTMPProtocolCodecFactory() {
        this.chunkMessageEncoder = new ChunkMessageEncoder();

        this.chunkMessageDecoder = new ChunkMessageDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return this.chunkMessageEncoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return this.chunkMessageDecoder;
    }
}
