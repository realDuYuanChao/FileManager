package com.github.shellhub.filemanager.event;

import com.github.shellhub.filemanager.entity.FileEntity;

import lombok.Data;

@Data
public class RenameEvent {
    private FileEntity fileEntity;
    private int position;

    public RenameEvent(FileEntity fileEntity, int position) {
        this.fileEntity = fileEntity;
        this.position = position;
    }
}
