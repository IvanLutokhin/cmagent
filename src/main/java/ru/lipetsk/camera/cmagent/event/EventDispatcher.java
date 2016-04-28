package ru.lipetsk.camera.cmagent.event;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ivan on 22.03.2016.
 */
public class EventDispatcher implements IEventDispatcher {
    private Map<String, List<IEventListener>> eventListenerMap;

    public EventDispatcher() {
        this.eventListenerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void addEventListener(String eventName, IEventListener eventListener) {
        if (!this.hasEventListener(eventName)) {
            this.eventListenerMap.put(eventName, new Vector<>());
        }

        Vector<IEventListener> eventListeners = (Vector<IEventListener>) this.eventListenerMap.get(eventName);

        eventListeners.add(eventListener);
    }

    @Override
    public void dispatchEvent(IEvent event) {
        Vector<IEventListener> eventListeners = (Vector<IEventListener>) this.eventListenerMap.get(event.getName());

        if (eventListeners != null) {
            for (IEventListener eventListener : eventListeners) {
                eventListener.onEventHandle(event);
            }
        }
    }

    @Override
    public boolean hasEventListener(String eventName) {
        return this.eventListenerMap.containsKey(eventName);
    }

    @Override
    public void removeEventListener(String eventName, IEventListener eventListener) {
        if (this.hasEventListener(eventName)) {
            Vector<IEventListener> eventListeners = (Vector<IEventListener>) this.eventListenerMap.get(eventName);

            for (int i = 0; i < eventListeners.size(); i++) {
                IEventListener currentEventListener = eventListeners.get(i);

                if (currentEventListener == eventListener) {
                    eventListeners.remove(eventListener);
                }
            }

            if (eventListeners.isEmpty()) {
                this.eventListenerMap.remove(eventName);
            }
        }
    }
}