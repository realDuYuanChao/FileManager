package com.github.shellhub.filemanager.presenter;

import com.github.shellhub.filemanager.event.FileActionEvent;

public interface MainPresenter {
    void loadFiles(String path);

    void loadParent();

    void handleFileAction(FileActionEvent fileActionEvent);
}
