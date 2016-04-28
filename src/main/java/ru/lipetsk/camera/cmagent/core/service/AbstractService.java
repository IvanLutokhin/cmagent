package ru.lipetsk.camera.cmagent.core.service;

import ru.lipetsk.camera.cmagent.core.facade.FacadeHolder;

/**
 * Created by Ivan on 24.03.2016.
 */
public abstract class AbstractService extends FacadeHolder {
    public AbstractService() {
        this.initialize();
    }

    protected abstract void initialize();
}