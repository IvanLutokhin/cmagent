package ru.lipetsk.camera.cmagent.core.proxy;

/**
 * Created by Ivan on 22.03.2016.
 */
public interface IProxy {
    String getName();

    Object getValueObject();

    void setValueObject(Object valueObject);

    void onRegister();

    void onUnregister();
}