package ru.lipetsk.camera.cmagent.core.proxy;

import ru.lipetsk.camera.cmagent.core.proxy.vo.StreamVO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ivan on 23.03.2016.
 */
public class StreamProxy extends Proxy {
    public StreamProxy() {
        super("StreamProxy", new ConcurrentHashMap<Integer, StreamVO>());
    }

    public Map<Integer, StreamVO> data() {
        return (Map<Integer, StreamVO>) this.valueObject;
    }

    public void addStream(StreamVO streamVO) {
        this.data().put(streamVO.getId(), streamVO);
    }

    public void addStream(int id, String enterpriseName, String url, int rtspPort, boolean record, boolean screenshot) {
        this.addStream(new StreamVO(id, enterpriseName, url, rtspPort, record, screenshot));
    }

    public StreamVO retrieveStream(int id) {
        return this.data().get(id);
    }

    public boolean hasStream(int id) {
        return this.data().containsKey(id);
    }

    public StreamVO removeStream(int id) {
        return this.data().remove(id);
    }

    @Override
    public void onRegister() { }

    @Override
    public void onUnregister() { }
}