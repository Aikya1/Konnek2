package com.aikya.konnek2.utils;

public interface MimeType {

    String IMAGE_MIME = "image/*";
    String IMAGE_MIME_JPEG = "image/jpeg";
    String IMAGE_MIME_PNG = "image/png";
    String VIDEO_MIME_MP4 = "video/mp4";
    String AUDIO_MIME_MP3 = "audio/mpeg";
    String[] mediaMimeTypes = {IMAGE_MIME_JPEG, IMAGE_MIME_PNG, VIDEO_MIME_MP4, AUDIO_MIME_MP3};

    String STREAM_MIME = "application/octet-stream";

    String IMAGE_MIME_PREFIX = "image";
    String VIDEO_MIME_EXTENSION_MP4 = "mp4";
    String AUDIO_MIME_EXTENSION_MP3 = "mp3";

    String DOC_MIME = "docx";
    String PDF_MIME = "pdf";
    String DOC_MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    String APPLICATION = "application";
}