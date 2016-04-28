package ru.lipetsk.camera.cmagent.net.rtsp.message;

/**
 * Created by Ivan on 11.02.2016.
 */
public class Header {
    private String key;

    private String value;

    public Header(String line) {
        int index = line.indexOf(':');

        if (index == -1) {
            this.key = line;
        } else {
            this.key = line.substring(0, index);

            this.value = line.substring(++index).trim();
        }
    }

    public Header(String key, String value) {
        this.key = key;

        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        if (obj instanceof String) {
            return this.getKey().equals(obj);
        }

        if (obj instanceof Header) {
            return this.getKey().equals(((Header) obj).getKey());
        }

        return false;
    }

    @Override
    public String toString() {
        return this.key + ": " + this.value;
    }
}
