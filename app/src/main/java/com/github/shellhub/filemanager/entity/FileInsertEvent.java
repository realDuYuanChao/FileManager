package com.github.shellhub.filemanager.entity;

import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public class FileInsertEvent {
    private int position;
    private FileEntity fileEntity;

    public FileInsertEvent(int position, FileEntity fileEntity) {
        this.position = position;
        this.fileEntity = fileEntity;
    }
}
