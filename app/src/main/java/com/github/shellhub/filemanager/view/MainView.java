package com.github.shellhub.filemanager.view;

import com.github.shellhub.filemanager.entity.FileEntity;

import java.util.List;

public interface MainView {
    void showFiles(List<FileEntity> fileEntities);


    void exit();
}
