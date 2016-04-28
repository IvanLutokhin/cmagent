package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMFObject;

/**
 * Created by Ivan on 20.02.2016.
 */
public abstract class Metadata extends Message {
    protected String name;

    protected AMFObject amfObject;

    public Metadata(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public Metadata(String name, AMFObject amfObject) {
        this.name = name;

        this.amfObject = amfObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AMFObject getAmfObject() {
        return amfObject;
    }

    public Object getItem(String key) {
        if (this.amfObject != null) {
            return this.amfObject.get(key);
        }

        return null;
    }

    public void setAmfObject(AMFObject amfObject) {
        this.amfObject = amfObject;
    }
}
