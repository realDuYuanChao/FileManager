package com.github.shellhub.filemanager.model;

import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.event.FileActionEvent;
import com.github.shellhub.filemanager.event.RenameEvent;
import com.github.shellhub.filemanager.presenter.impl.MainPresenterImpl;

import java.util.List;

public interface MainModel {

    interface Callback{
        void onLoadFiles(List<FileEntity> fileEntities);

        void onShouldBackHome();

        void onRenameCompleted(RenameEvent renameEvent);

        void onAudioLoad(String audioPath);

        void onDeleted(int position);

        void onFolderCreated(FileEntity fileEntity);

        void onFileCreated(FileEntity fileEntity);
    }

    void loadFiles(String rootPath, Callback callback);

    void loadParent(Callback callback);

    void handleFileAction(FileActionEvent fileActionEvent, Callback callback);

    void createFolder(String name, Callback callback);

    void createFile(String name, Callback callback);
}
