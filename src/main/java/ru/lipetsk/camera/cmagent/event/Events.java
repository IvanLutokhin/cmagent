package ru.lipetsk.camera.cmagent.event;

/**
 * Created by Ivan on 24.03.2016.
 */
public class Events {
    public static final String RTP_PACKET = "rtp.packet";

    public static final String RTCP_PACKET = "rtcp.packet";

    public static final String H264_VIDEO_FRAME = "h264_video_frame";

    public static final String CONNECTION_OPENED = "connection_opened";

    public static final String CONNECTION_CLOSED = "connection_closed";

    public static final String MESSAGE_RECEIVED = "message_received";

    public static final String MESSAGE_SENT = "message_sent";

    public static final String RTMP_CREATE_STREAM = "rtmp.create_stream";

    public static final String RTMP_PUBLISH_STREAM = "rtmp.publish_stream";

    public static final String RTMP_NO_DATA = "rtmp.no_data";
}