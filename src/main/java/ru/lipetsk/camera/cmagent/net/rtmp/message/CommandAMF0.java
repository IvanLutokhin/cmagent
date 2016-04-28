package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMFObject;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMFValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 18.02.2016.
 */
public class CommandAMF0 extends Command {
    public CommandAMF0(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public CommandAMF0(String commandName, int transactionId, AMFObject commandObject, Object... userArguments) {
        super(commandName, transactionId, commandObject, userArguments);
    }

    public CommandAMF0(String commandName, AMFObject commandObject, Object... userArguments) {
        super(commandName, commandObject, userArguments);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.COMMAND_AMF0;
    }

    @Override
    public void decode(IoBuffer ioBuffer) {
        this.commandName = (String) AMFValue.decode(ioBuffer);

        this.transactionId = ((Double) AMFValue.decode(ioBuffer)).intValue();

        this.commandObject = (AMFObject) AMFValue.decode(ioBuffer);

        List<Object> objects = new ArrayList<>();

        while (ioBuffer.hasRemaining()) {
            objects.add(AMFValue.decode(ioBuffer));
        }

        this.userArguments = objects.toArray();
    }

    @Override
    public IoBuffer encode() {
        IoBuffer ioBuffer = IoBuffer.allocate(1);

        ioBuffer.setAutoExpand(true);

        AMFValue.encode(ioBuffer, this.commandName, this.transactionId, this.commandObject);

        if (this.userArguments != null) {
            for (Object object : this.userArguments) {
                AMFValue.encode(ioBuffer, object);
            }
        }

        ioBuffer.flip();

        return ioBuffer;
    }
}
