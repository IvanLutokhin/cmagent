package ru.lipetsk.camera.cmagent.net.sdp;

import ru.lipetsk.camera.cmagent.net.sdp.field.*;
import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.*;

/**
 * Created by Ivan on 12.02.2016.
 */
public class SessionDescription {
    public static SessionDescription parse(String input) throws SDPParseException {
        return parse(input.split("\r\n"));
    }

    public static SessionDescription parse(String[] fields) throws SDPParseException {
        int index = 0;

        SessionDescription sessionDescription = null;

        TimeDescription timeDescription = null;

        MediaDescription mediaDescription = null;

        boolean getMoreFields = false;

        boolean getMoreDescriptions = false;

        try {
            sessionDescription = new SessionDescription();

            sessionDescription.setVersion(Version.parse(fields[index]));
            sessionDescription.setOrigin(Origin.parse(fields[++index]));
            sessionDescription.setSessionName(SessionName.parse(fields[++index]));

            try {
                sessionDescription.setSessionInformation(SessionInformation.parse(fields[++index]));
            } catch (SDPParseException e) {
                --index;
            }

            try {
                sessionDescription.setURI(URI.parse(fields[++index]));
            } catch (SDPParseException e) {
                --index;
            }

            while (true) {
                try {
                    sessionDescription.addEmail(Email.parse(fields[++index]));
                } catch (SDPParseException e) {
                    --index;

                    break;
                }
            }

            while (true) {
                try {
                    sessionDescription.addPhone(Phone.parse(fields[++index]));
                } catch (SDPParseException e) {
                    --index;

                    break;
                }
            }

            try {
                sessionDescription.setConnectionData(ConnectionData.parse(fields[++index]));
            } catch (SDPParseException e) {
                --index;
            }

            while (true) {
                try {
                    sessionDescription.addBandwidth(Bandwidth.parse(fields[++index]));
                } catch (SDPParseException e) {
                    --index;

                    break;
                }
            }

            timeDescription = new TimeDescription(Time.parse(fields[++index]));

            sessionDescription.addTimeDescription(timeDescription);

            getMoreFields = index < fields.length;

            while (getMoreFields) {
                try {
                    timeDescription.addRepeatTime(RepeatTime.parse(fields[++index]));
                } catch (SDPParseException e) {
                    --index;

                    getMoreFields = false;
                }
            }

            getMoreDescriptions = index < fields.length;

            while (getMoreDescriptions) {
                try {
                    timeDescription = new TimeDescription(Time.parse(fields[++index]));

                    sessionDescription.addTimeDescription(timeDescription);

                    getMoreFields = index < fields.length;

                    while(getMoreFields) {
                        try {
                            timeDescription.addRepeatTime(RepeatTime.parse(fields[++index]));
                        } catch (SDPParseException e) {
                            --index;

                            getMoreFields = false;
                        }
                    }
                } catch (SDPParseException e) {
                    --index;

                    getMoreDescriptions = false;
                }
            }

            try {
                sessionDescription.setTimeZone(ru.lipetsk.camera.cmagent.net.sdp.field.TimeZone.parse(fields[++index]));
            } catch (SDPParseException e) {
                --index;
            }

            try {
                sessionDescription.setKey(Key.parse(fields[++index]));
            } catch (SDPParseException e) {
                --index;
            }

            getMoreFields = index < fields.length;

            while(getMoreFields) {
                try {
                    sessionDescription.addAttribute(Attribute.parse(fields[++index]));
                } catch (SDPParseException e) {
                    --index;

                    getMoreFields = false;
                }
            }

            getMoreDescriptions = index < fields.length;

            while(getMoreDescriptions) {
                try {
                    mediaDescription = new MediaDescription(Media.parse(fields[++index]));

                    try {
                        mediaDescription.setSessionInformation(SessionInformation.parse(fields[++index]));
                    } catch (SDPParseException e) {
                        --index;
                    }

                    try {
                        mediaDescription.setConnectionData(ConnectionData.parse(fields[++index]));
                    } catch (SDPParseException e) {
                        --index;
                    }

                    getMoreFields = true;

                    while(getMoreFields) {
                        try {
                            mediaDescription.addBandwidth(Bandwidth.parse(fields[++index]));
                        } catch (SDPParseException e) {
                            --index;

                            getMoreFields = false;
                        }
                    }

                    try {
                        mediaDescription.setKey(Key.parse(fields[++index]));
                    } catch (SDPParseException e) {
                        --index;
                    }

                    getMoreFields = index < fields.length;

                    while(getMoreFields) {
                        try {
                            mediaDescription.addAttribute(Attribute.parse(fields[++index]));

                            getMoreFields = index + 1 < fields.length;
                        } catch (SDPParseException e) {
                            --index;

                            getMoreFields = false;
                        }
                    }

                    sessionDescription.addMediaDescription(mediaDescription);

                    getMoreDescriptions = index + 1 < fields.length;
                } catch (SDPParseException e) {
                    --index;

                    getMoreDescriptions = false;

                    if (index < fields.length) {
                        throw new SDPParseException("Cannot parse a media description", e);
                    }
                }
            }
        } catch (SDPParseException | SDPException e) {
            throw new SDPParseException("Invalid session description", e);
        }

        return sessionDescription;
    }

    private Version v;

    private Origin o;

    private SessionName s;

    private SessionInformation i;

    private URI u;

    private List<Email> emails;

    private List<Phone> phones;

    private ConnectionData c;

    private Map<String, Bandwidth> bandwidthMap;

    private List<TimeDescription> timeDescriptions;

    private ru.lipetsk.camera.cmagent.net.sdp.field.TimeZone z;

    private Key k;

    private Map<String, Object> sessionAttributes;

    private List<MediaDescription> mediaDescriptions;

    public SessionDescription() {
        this.emails = new ArrayList<>();

        this.phones = new ArrayList<>();

        this.bandwidthMap = new HashMap<>();

        this.timeDescriptions = new ArrayList<>();

        this.sessionAttributes = new HashMap<>();

        this.mediaDescriptions = new ArrayList<>();
    }

    public Version getVersion() {
        return this.v;
    }

    public void setVersion(Version v) throws IllegalArgumentException {
        if (v == null) {
            throw new IllegalArgumentException("Version field cannot be null");
        }

        this.v = v;
    }

    public Origin getOrigin() {
        return this.o;
    }

    public void setOrigin(Origin o) throws IllegalArgumentException {
        if (o == null) {
            throw new IllegalArgumentException("Origin field cannot be null");
        }

        this.o = o;
    }

    public SessionName getSessionName() {
        return this.s;
    }

    public void setSessionName(SessionName s) throws IllegalArgumentException {
        if (s == null) {
            throw new IllegalArgumentException("Session name field cannot be null");
        }

        this.s = s;
    }

    public SessionInformation getSessionInformation() {
        return this.i;
    }

    public void setSessionInformation(SessionInformation i) throws IllegalArgumentException {
        if (i == null) {
            throw new IllegalArgumentException("Session information field cannot be null");
        }

        this.i = i;
    }

    public URI getURI() {
        return this.u;
    }

    public void setURI(URI u) throws IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("URI field cannot be null");
        }

        this.u = u;
    }

    public Email[] getEmails() {
        return this.emails.toArray(new Email[this.emails.size()]);
    }

    public void setEmails(Email[] fields) throws IllegalArgumentException {
        if (fields == null) {
            throw new IllegalArgumentException("Email fields cannot be null");
        }

        this.emails.clear();

        for (Email email : fields) {
            this.addEmail(email);
        }
    }

    public void addEmail(Email field) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("An email field cannot be null");
        }

        this.emails.add(field);
    }

    public Phone[] getPhones() {
        return this.phones.toArray(new Phone[this.phones.size()]);
    }

    public void setPhones(Phone[] fields) throws IllegalArgumentException {
        if (fields == null) {
            throw new IllegalArgumentException("Phone fields cannot be null");
        }

        this.phones.clear();

        for (Phone phone : fields) {
            this.addPhone(phone);
        }
    }

    public void addPhone(Phone field) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("A phone field cannot be null");
        }

        this.phones.add(field);
    }

    public ConnectionData getConnectionData() {
        return this.c;
    }

    public boolean hasConnectionData() {
        return this.c != null;
    }

    public void setConnectionData(ConnectionData c) {
        if (c == null) {
            throw new IllegalArgumentException("A connection data cannot be null");
        }

        this.c = c;
    }

    public Bandwidth getBandwidth(String type) {
        return this.bandwidthMap.get(type);
    }

    public Bandwidth[] getBandwidths() {
        return this.bandwidthMap.values().toArray(new Bandwidth[this.bandwidthMap.values().size()]);
    }

    public void setBandwidths(Bandwidth[] fields) {
        if (fields == null) {
            throw new IllegalArgumentException("Bandwith fields cannot be null");
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

    public TimeDescription[] getTimeDescriptions() {
        return this.timeDescriptions.toArray(new TimeDescription[this.timeDescriptions.size()]);
    }

    public void setTimeDescriptions(TimeDescription[] descriptions) throws IllegalArgumentException {
        if (descriptions == null) {
            throw new IllegalArgumentException("Time descriptions cannot be null");
        }

        this.timeDescriptions.clear();

        for (TimeDescription timeDescription : descriptions) {
            this.addTimeDescription(timeDescription);
        }
    }

    public void addTimeDescription(TimeDescription timeDescription) throws IllegalArgumentException {
        if (timeDescription == null) {
            throw new IllegalArgumentException("A time description cannot be null");
        }

        this.timeDescriptions.add(timeDescription);
    }

    public ru.lipetsk.camera.cmagent.net.sdp.field.TimeZone getTimeZone() {
        return this.z;
    }

    public void setTimeZone(ru.lipetsk.camera.cmagent.net.sdp.field.TimeZone z) {
        if (z == null) {
            throw new IllegalArgumentException("Time zone field cannot be null");
        }

        this.z = z;
    }

    public Key getKey() {
        return this.k;
    }

    public void setKey(Key k) {
        if (k == null) {
            throw new IllegalArgumentException("Key field cannot be null");
        }

        this.k = k;
    }

    public Attribute[] getAttributes() {
        Object attribute = null;

        List values = null;

        List result = new ArrayList<>();

        Iterator i = this.sessionAttributes.values().iterator();

        while(true) {
            while(i.hasNext()) {
                attribute = i.next();

                if(attribute instanceof Attribute) {
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

    public Attribute getAttribute(String name) {
        Attribute result = null;

        Object attribute = this.sessionAttributes.get(name);

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

        Object attribute = this.sessionAttributes.get(name);

        if(attribute != null && attribute instanceof List) {
            List values = (List)attribute;

            result = (Attribute)values.get(index);
        }

        return result;
    }

    public Attribute[] getAttributes(String name) {
        Object attribute = this.sessionAttributes.get(name);

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

        Object attribute = this.sessionAttributes.get(name);

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
            result = this.sessionAttributes.containsKey(name);
        }

        return result;
    }

    public void setAttributes(Attribute[] fields) {
        if (fields == null) {
            throw new IllegalArgumentException("Attribute fields cannot be null");
        }

        this.sessionAttributes.clear();

        for (Attribute attribute : fields) {
            this.addAttribute(attribute);
        }
    }

    public void addAttribute(Attribute field) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("An attribute field cannot be null");
        }

        String name = field.getName();

        if (this.sessionAttributes.containsKey(name)) {
            Object values = null;

            Object sessionAttribute = this.sessionAttributes.get(name);

            if (sessionAttribute instanceof List) {
                values = (List) sessionAttribute;
            } else {
                Attribute attribute = (Attribute)sessionAttribute;

                values = new ArrayList<>();

                ((List) values).add(attribute);

                this.sessionAttributes.put(field.getName(), values);
            }

            ((List) values).add(field);
        } else {
            this.sessionAttributes.put(field.getName(), field);
        }
    }

    public MediaDescription[] getMediaDescriptions() {
        return this.mediaDescriptions.toArray(new MediaDescription[this.mediaDescriptions.size()]);
    }

    public int getMediaDescriptionsCount() {
        return this.mediaDescriptions.size();
    }

    public void setMediaDescriptions(MediaDescription[] descriptions) throws IllegalArgumentException, SDPException {
        if (descriptions == null) {
            throw new IllegalArgumentException("Media descriptions cannot be null");
        }

        this.mediaDescriptions.clear();

        for (MediaDescription mediaDescription : descriptions) {
            this.addMediaDescription(mediaDescription);
        }
    }

    public void addMediaDescription(MediaDescription mediaDescription) throws IllegalArgumentException, SDPException {
        if (mediaDescription == null) {
            throw new IllegalArgumentException("A media description cannot be null");
        } /*else if (!this.hasConnectionData() && !mediaDescription.hasConnectionData()) {
            throw new SDPException("This media description must have a connection field");
        } */else {
            this.mediaDescriptions.add(mediaDescription);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.v.toString()).append("\r\n");

        stringBuilder.append(this.o.toString()).append("\r\n");

        stringBuilder.append(this.s.toString()).append("\r\n");

        if(this.i != null) {
            stringBuilder.append(this.i.toString()).append("\r\n");
        }

        if(this.u != null) {
            stringBuilder.append(this.u.toString()).append("\r\n");
        }

        for (Email email : this.emails) {
            stringBuilder.append(email.toString()).append("\r\n");
        }

        for (Phone phone : this.phones) {
            stringBuilder.append(phone.toString()).append("\r\n");
        }

        if(this.c != null) {
            stringBuilder.append(this.c.toString()).append("\r\n");
        }

        for (Bandwidth bandwidth: this.bandwidthMap.values()) {
            stringBuilder.append(bandwidth.toString()).append("\r\n");
        }

        for (TimeDescription timeDescription : this.timeDescriptions) {
            stringBuilder.append(timeDescription.toString()).append("\r\n");
        }

        if(this.z != null) {
            stringBuilder.append(this.z.toString()).append("\r\n");
        }

        if(this.k != null) {
            stringBuilder.append(this.k.toString()).append("\r\n");
        }

        for (Attribute attribute : this.getAttributes()) {
            stringBuilder.append(attribute.toString()).append("\r\n");
        }

        for (MediaDescription mediaDescription : this.mediaDescriptions) {
            stringBuilder.append(mediaDescription.toString()).append("\r\n");
        }

        return stringBuilder.toString();
    }
}
