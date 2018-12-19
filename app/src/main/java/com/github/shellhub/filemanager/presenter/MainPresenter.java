package com.github.shellhub.filemanager.presenter;

import com.github.shellhub.filemanager.entity.ScrollEvent;
import com.github.shellhub.filemanager.event.FileActionEvent;

public interface MainPresenter {
    void loadFiles(String path);

    void loadParent();

    void handleFileAction(FileActionEvent fileActionEvent);

    void handleScrollEvent(ScrollEvent event);

    void createFolder(String name);

    void createFile(String name);
}
