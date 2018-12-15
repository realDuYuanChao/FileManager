package com.github.shellhub.filemanager.utils;

import com.github.shellhub.filemanager.entity.FileType;

import java.io.File;

public class FileUtils {
    public static FileType getFileType(File file) {
        FileType fileType = null;
        if (file.isDirectory()) {
            fileType = FileType.TYPE_FOLDER;
        }else{
            String filename = file.getName().toLowerCase();
            if (filename.endsWith("jpg") || filename.endsWith("jpeg") || filename.endsWith("png")) {
                fileType = FileType.TYPE_IMAGE;
            } else if (filename.endsWith("pdf")) {
                fileType = FileType.TYPE_PDF;
            } else if (filename.endsWith("mp3") || filename.endsWith("oog")) {
                fileType = FileType.TYPE_AUDIO;
            } else if (filename.endsWith("mp4") || filename.endsWith("avi")) {
                fileType = FileType.TYPE_VIDEO;
            } else {
                fileType = FileType.TYPE_UNKNOWN;
            }
        }
        return fileType;
    }
}
