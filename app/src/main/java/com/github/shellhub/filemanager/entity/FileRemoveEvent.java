package com.github.shellhub.filemanager.entity;

import lombok.Getter;
import lombok.Setter;

public class FileRemoveEvent {
    @Setter@Getter
    int position;

    public FileRemoveEvent(int position) {
        this.position = position;
    }
}
