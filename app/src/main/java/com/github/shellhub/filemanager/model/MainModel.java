package com.github.shellhub.filemanager.model;

import com.github.shellhub.filemanager.entity.FileEntity;

import java.util.List;

public interface MainModel {
    interface Callback{
        void onLoadFiles(List<FileEntity> fileEntities);

        void onShouldBackHome();
    }

    void loadFiles(String rootPath, Callback callback);

    void loadParent(Callback callback);
}
