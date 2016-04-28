package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0.AMFObject;

/**
 * Created by Ivan on 18.02.2016.
 */
public abstract class Command extends Message {
    protected String commandName;

    protected int transactionId;

    protected AMFObject commandObject;

    protected Object[] userArguments;

    public Command(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public Command(String commandName, int transactionId, AMFObject commandObject, Object... userArguments) {
        this.commandName = commandName;

        this.transactionId = transactionId;

        this.commandObject = commandObject;

        this.userArguments = userArguments;
    }

    public Command(String commandName, AMFObject commandObject, Object... userArguments) {
        this(commandName, 0, commandObject, userArguments);
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public AMFObject getCommandObject() {
        return commandObject;
    }

    public void setCommandObject(AMFObject commandObject) {
        this.commandObject = commandObject;
    }

    public Object[] getUserArguments() {
        return userArguments;
    }

    public Object getUserArgument(int index) {
        return this.userArguments[index];
    }

    public int getUserArgumentCount() {
        return (this.userArguments != null) ? this.userArguments.length : 0;
    }

    public void setUserArguments(Object[] userArguments) {
        this.userArguments = userArguments;
    }

    public String toString() {
        return this.commandName + " [ " + this.transactionId + " ]";
    }
}