package com.github.shellhub.filemanager.utils;

import com.github.shellhub.filemanager.entity.FileType;

import java.io.File;
import java.text.DecimalFormat;

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
            } else if (filename.endsWith("txt")) {
                fileType = FileType.TYPE_TEXT;
            } else {
                fileType = FileType.TYPE_UNKNOWN;
            }
        }
        return fileType;
    }

    public static String formatFileSize(long size) {
        String hrSize;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i+1);
        }
        return extension;
    }
}
