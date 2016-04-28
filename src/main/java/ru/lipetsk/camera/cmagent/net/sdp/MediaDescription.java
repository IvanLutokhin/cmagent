package ru.lipetsk.camera.cmagent.net.sdp;

import ru.lipetsk.camera.cmagent.net.sdp.field.*;

import java.util.*;

/**
 * Created by Ivan on 13.02.2016.
 */
public class MediaDescription {
    private Media m;
    private SessionInformation i;
    private ConnectionData c;
    private Map<String, Bandwidth> bandwidthMap;
    private Key k;
    private Map<String, Object> attributes;

    public MediaDescription(Media media) throws IllegalArgumentException {
        this.bandwidthMap = new HashMap<>();

        this.attributes = new HashMap<>();

        this.setMedia(media);
    }

    public Media getMedia() {
        return this.m;
    }

    public void setMedia(Media media) throws IllegalArgumentException {
        if (media == null) {
            throw new IllegalArgumentException("The media field cannot be null");
        }

        this.m = media;
    }

    public SessionInformation getSessionInformation() {
        return this.i;
    }

    public boolean hasSessionInformation() {
        return this.i != null;
    }

    public void setSessionInformation(SessionInformation sessionInformation) {
        this.i = sessionInformation;
    }

    public ConnectionData getConnectionData() {
        return this.c;
    }

    public boolean hasConnectionData() {
        return this.c != null;
    }

    public void setConnectionData(ConnectionData connectionData) {
        this.c = connectionData;
    }

    public Bandwidth getBandwidth(String type) {
        return this.bandwidthMap.get(type);
    }

    public Bandwidth[] getBandwidths() {
        return this.bandwidthMap.values().toArray(new Bandwidth[this.bandwidthMap.size()]);
    }

    public void setBandwidths(Bandwidth[] fields) {
        if (fields == null) {
            throw new IllegalArgumentException("Bandwidth fields cannot be null");
        }

        this.bandwidthMap.clear();

        for (Bandwidth bandwidth : fields) {
            this.addBandwidth(bandwidth);
        }
    }

    public void addBandwidth(Bandwidth field) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("A bandwidth field cannot be null");
        }

        this.bandwidthMap.put(field.getType(), field);
    }

    public void clearBandwidths() {
        this.bandwidthMap.clear();
    }

    public Key getKey() {
        return this.k;
    }

    public boolean hasKey() {
        return this.k != null;
    }

    public void setKey(Key k) {
        this.k = k;
    }

    public Attribute getAttribute(String name) {
        Attribute result = null;

        Object attribute = this.attributes.get(name);

        if(attribute != null) {
            if(attribute instanceof List) {
                List values = (List)attribute;

                result = (Attribute)values.get(0);
            } else {
                result = (Attribute)attribute;
            }
        }

        return result;
    }

    public Attribute getAttribute(String name, int index) {
        Attribute result = null;

        Object attribute = this.attributes.get(name);

        if(attribute != null && attribute instanceof List) {
            List values = (List)attribute;

            result = (Attribute)values.get(index);
        }

        return result;
    }

    public Attribute[] getAttributes() {
        Object attribute = null;

        List values = null;

        List result = new ArrayList<>();

        Iterator i = this.attributes.values().iterator();

        while(true) {
            while(i.hasNext()) {
                attribute = i.next();

                if (attribute instanceof Attribute) {
                    result.add(attribute);
                } else {
                    values = (List)attribute;

                    Iterator j = values.iterator();

                    while(j.hasNext()) {
                        result.add(j.next());
                    }
                }
            }

            return (Attribute[]) result.toArray(new Attribute[result.size()]);
        }
    }

    public Attribute[] getAttributes(String name) {
        Object attribute = this.attributes.get(name);

        Attribute[] result;

        if(attribute != null) {
            if(attribute instanceof List) {
                List values = (List)attribute;

                result = (Attribute[]) values.toArray(new Attribute[values.size()]);
            } else {
                result = new Attribute[]{(Attribute)attribute};
            }
        } else {
            result = new Attribute[0];
        }

        return result;
    }

    public int getAttributesCount(String name) {
        int result = 0;

        Object attribute = this.attributes.get(name);

        if(attribute != null) {
            if(attribute instanceof List) {
                List values = (List)attribute;

                result = values.size();
            } else {
                result = 1;
            }
        }

        return result;
    }

    public boolean hasAttribute(String name) {
        boolean result = false;

        if(name != null) {
            result = this.attributes.containsKey(name);
        }

        return result;
    }

    public void setAttributes(Attribute[] fields) {
        if (fields == null) {
            throw new IllegalArgumentException("Attribute fields cannot be null");
        }

        this.attributes.clear();

        for(Attribute attribute : fields) {
            this.addAttribute(attribute);
        }
    }

    public void addAttribute(Attribute field) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("An attribute field cannot be null");
        }

        String name = field.getName();

        if (this.attributes.containsKey(name)) {
            Object values = null;

            Object attribute = this.attributes.get(name);

            if (attribute instanceof List) {
                values = (List)attribute;
            } else {
                Attribute previous = (Attribute)attribute;

                values = new ArrayList<>();

                ((List)values).add(previous);

                this.attributes.put(field.getName(), values);
            }

            ((List)values).add(field);
        } else {
            this.attributes.put(field.getName(), field);
        }
    }

    public void clearAttributes() {
        this.attributes.clear();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.m.toString());

        stringBuilder.append("\r\n");

        if(this.i != null) {
            stringBuilder.append(this.i.toString()).append("\r\n");
        }

        if(this.c != null) {
            stringBuilder.append(this.c.toString()).append("\r\n");
        }

        for (Bandwidth bandwidth : this.getBandwidths()) {
            stringBuilder.append(bandwidth).append("\r\n");
        }

        if(this.k != null) {
            stringBuilder.append(this.k.toString()).append("\r\n");
        }

        for (Attribute attribute : this.getAttributes()) {
            stringBuilder.append(attribute).append("\r\n");
        }

        return stringBuilder.toString();
    }
}
