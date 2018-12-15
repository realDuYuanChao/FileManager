package com.github.shellhub.filemanager.event;

import com.github.shellhub.filemanager.entity.FileEntity;

import java.util.List;

import lombok.Getter;

public class FileEntitiesEvent {
    @Getter
    List<FileEntity> fileEntities;

    public FileEntitiesEvent(List<FileEntity> fileEntities) {
        this.fileEntities = fileEntities;
    }
}
