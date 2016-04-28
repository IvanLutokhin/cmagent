package ru.lipetsk.camera.cmagent.event;

/**
 * Created by Ivan on 22.03.2016.
 */
public class Event implements IEvent {
    protected String name;

    protected Object context;

    protected IEventDispatcher owner;

    public Event(String name, Object context, IEventDispatcher owner) {
        this.name = name;

        this.context = context;

        this.owner = owner;
    }

    public Event(String name, Object context) {
        this(name, context, null);
    }

    public Event(String name) {
        this(name, null);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getContext() {
        return this.context;
    }

    @Override
    public IEventDispatcher getOwner() {
        return this.owner;
    }
}