package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMFObject;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMFValue;

/**
 * Created by Ivan on 20.02.2016.
 */
public class MetadataAMF0 extends Metadata {
    public MetadataAMF0(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public MetadataAMF0(String name, AMFObject amfObject) {
        super(name, amfObject);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.METADATA_AMF0;
    }

    @Override
    public void decode(IoBuffer ioBuffer) {
        this.name = (String) AMFValue.decode(ioBuffer);

        this.amfObject = (AMFObject) AMFValue.decode(ioBuffer);
    }

    @Override
    public IoBuffer encode() {
        IoBuffer ioBuffer = IoBuffer.allocate(1);

        ioBuffer.setAutoExpand(true);

        AMFValue.encode(ioBuffer, this.name);

        AMFValue.encode(ioBuffer, "onMetaData");

        AMFValue.encode(ioBuffer, this.amfObject);

        ioBuffer.flip();

        return ioBuffer;
    }
}
