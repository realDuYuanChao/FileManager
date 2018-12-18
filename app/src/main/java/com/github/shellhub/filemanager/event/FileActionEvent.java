package com.github.shellhub.filemanager.event;

import com.github.shellhub.filemanager.entity.FileAction;
import com.github.shellhub.filemanager.entity.FileEntity;

import lombok.Data;

@Data
public class FileActionEvent {
    private FileEntity fileEntity;
    private FileAction fileAction;

    public FileActionEvent(FileEntity fileEntity, FileAction fileAction, int position) {
        this.fileEntity = fileEntity;
        this.fileAction = fileAction;
        this.position = position;
    }

    public FileActionEvent() {
    }

    public FileActionEvent(FileEntity fileEntity, int position) {
        this.fileEntity = fileEntity;
        this.position = position;
    }

    private int position;

}
