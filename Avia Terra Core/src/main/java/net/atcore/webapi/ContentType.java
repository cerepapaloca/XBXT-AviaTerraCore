package net.atcore.webapi;

public enum ContentType {
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_SVG("image/svg+xml"),
    AUDIO_MPEG("audio/mpeg"),
    AUDIO_OGG("audio/ogg"),
    VIDEO_MP4("video/mp4"),
    VIDEO_WEBM("video/webm"),
    MULTIPART_FORM_DATA("multipart/form-data");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}