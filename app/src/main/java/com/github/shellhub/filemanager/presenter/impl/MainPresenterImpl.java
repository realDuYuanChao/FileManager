package com.github.shellhub.filemanager.presenter.impl;

import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.event.FileActionEvent;
import com.github.shellhub.filemanager.event.RenameEvent;
import com.github.shellhub.filemanager.model.MainModel;
import com.github.shellhub.filemanager.model.impl.MainModelImpl;
import com.github.shellhub.filemanager.presenter.MainPresenter;
import com.github.shellhub.filemanager.view.MainView;

import java.util.List;

public class MainPresenterImpl implements MainPresenter, MainModel.Callback{

    private MainModel mainModel;
    private MainView mainView;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
        mainModel = new MainModelImpl();

    }
    @Override
    public void loadFiles(String path) {
        mainModel.loadFiles(path, this);
    }

    @Override
    public void loadParent() {
        mainModel.loadParent(this);
    }

    @Override
    public void handleFileAction(FileActionEvent fileActionEvent) {
        mainModel.handleFileAction(fileActionEvent, this);
    }

    @Override
    public void onLoadFiles(List<FileEntity> fileEntities) {
        mainView.showFiles(fileEntities);
    }

    @Override
    public void onShouldBackHome() {
        mainView.exit();
    }

    @Override
    public void onRenameCompleted(RenameEvent renameEvent) {
        mainView.rename(renameEvent);
    }

    @Override
    public void onAudioLoad(String audioPath) {
        mainView.playAudio(audioPath);
    }
}
