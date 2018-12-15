package com.github.shellhub.filemanager.event;

import com.github.shellhub.filemanager.entity.FileEntity;

import lombok.Data;

@Data
public class FileEntityEvent {
    private FileEntity fileEntity;

    public FileEntityEvent(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }
}
