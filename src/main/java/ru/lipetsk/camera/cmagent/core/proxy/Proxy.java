package ru.lipetsk.camera.cmagent.core.proxy;

import ru.lipetsk.camera.cmagent.core.facade.FacadeHolder;

/**
 * Created by Ivan on 22.03.2016.
 */
public abstract class Proxy extends FacadeHolder implements IProxy {
    protected final String name;

    protected Object valueObject;

    public Proxy(String name, Object valueObject) {
        this.name = name;

        this.valueObject = valueObject;
    }

    public Proxy(String name) {
        this(name, null);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getValueObject() {
        return this.valueObject;
    }

    @Override
    public void setValueObject(Object valueObject) {
        this.valueObject = valueObject;
    }
}