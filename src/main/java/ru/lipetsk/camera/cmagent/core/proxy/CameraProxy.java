package ru.lipetsk.camera.cmagent.core.proxy;

import ru.lipetsk.camera.cmagent.core.proxy.vo.CameraVO;

/**
 * Created by Ivan on 23.03.2016.
 */
public class CameraProxy extends Proxy {
    public CameraProxy() {
        super("CameraProxy", new CameraVO());
    }

    public CameraVO data() {
        return (CameraVO) this.valueObject;
    }

    @Override
    public void onRegister() { }

    @Override
    public void onUnregister() { }
}