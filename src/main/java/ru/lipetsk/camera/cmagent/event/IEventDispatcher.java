package ru.lipetsk.camera.cmagent.event;

/**
 * Created by Ivan on 22.03.2016.
 */
public interface IEventDispatcher {
    void addEventListener(String eventName, IEventListener eventListener);

    void dispatchEvent(IEvent event);

    boolean hasEventListener(String eventName);

    void removeEventListener(String eventName, IEventListener eventListener);
}