package com.github.shellhub.filemanager.entity;

import lombok.Getter;
import lombok.Setter;

public class ScrollEvent {
    @Setter@Getter
    private int dy;

    public ScrollEvent(int dy) {
        this.dy = dy;
    }
}
