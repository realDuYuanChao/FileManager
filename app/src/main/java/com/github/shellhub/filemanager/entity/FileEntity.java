package com.github.shellhub.filemanager.entity;

import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.Stack;

import lombok.Data;

@Data
public class FileEntity {
    private String name;
    private String parentPath;
    private Stack<String> pathStack = new Stack<>();
    private String path;
    private long size; /*in bits*/
    private Drawable drawableIcon;
    private int resIcon;
    private FileType fileType;
    private int subCount;
}
