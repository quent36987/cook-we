package com.cookwe.utils.image;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ImageDetail {

    public static void printImageSizeInMbOrKb(byte[] imageBytes) throws IOException {
        int fileSizeInBytes = imageBytes.length;
        double fileSizeInKB = fileSizeInBytes / 1024.0;
        double fileSizeInMbit = fileSizeInBytes * 8.0 / 1_000_000.0;

        log.info("Image size: {} KB", fileSizeInKB);
        log.info("Image size: {} Mbit", fileSizeInMbit);
    }
}
