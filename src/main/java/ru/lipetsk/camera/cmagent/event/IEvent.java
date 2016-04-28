package ru.lipetsk.camera.cmagent.event;

/**
 * Created by Ivan on 22.03.2016.
 */
public interface IEvent {
    String getName();

    Object getContext();

    IEventDispatcher getOwner();
}