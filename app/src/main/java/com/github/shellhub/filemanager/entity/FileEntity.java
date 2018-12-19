package com.github.shellhub.filemanager.entity;

import android.graphics.drawable.Drawable;

import java.util.Stack;

import lombok.Data;

@Data
public class FileEntity {
    private String name;
    private String newName;
    private String parentPath;
    private Stack<String> pathStack = new Stack<>();
    private String path;
    private String newPath;
    private long size; /*in bits*/
    private String formatSize;
    private Drawable drawableIcon;
    private int resIcon;
    private FileType fileType;
    private int subCount;
    private long lastMidify;
    private byte[] embeddedPicture;
    private String duration;
    private String albumName;
    private String formatLastModify;
    private String subCountTitle;
    private String format;
}
