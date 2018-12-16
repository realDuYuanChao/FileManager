package com.github.shellhub.filemanager.model.impl;

import android.util.Log;

import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.entity.FileType;
import com.github.shellhub.filemanager.model.MainModel;
import com.github.shellhub.filemanager.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainModelImpl implements MainModel {

    private String TAG = this.getClass().getSimpleName();
    private Stack<String> pathStack = new Stack<>();
    private boolean shouldPush = true;

    @Override
    public void loadFiles(String rootPath, Callback callback) {
        File file = new File(rootPath);
        if (!file.isDirectory()) {
            return;
        }

        Log.d(TAG, "loadFiles: " + pathStack);

        List<FileEntity> fileEntities = new ArrayList<>();
        Observable.create((ObservableOnSubscribe<FileEntity>) emitter -> {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileEntity fileEntity = new FileEntity();
                fileEntity.setParentPath(rootPath);
                fileEntity.setName(files[i].getName());
                fileEntity.setPath(files[i].getPath());
                fileEntity.setFileType(FileUtils.getFileType(files[i]));
                if (files[i].isDirectory()) {
                    fileEntity.setSubCount(files[i].listFiles().length);
                    fileEntity.setLastMidify(files[i].lastModified());
                }
                emitter.onNext(fileEntity);
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<FileEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(FileEntity fileEntity) {
                fileEntities.add(fileEntity);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                callback.onLoadFiles(sortByType(fileEntities));
                if (shouldPush) {
                    pathStack.push(rootPath);
                    shouldPush = true;
                }
            }
        });
    }

    @Override
    public void loadParent(Callback callback) {
        shouldPush = false;
        if (!pathStack.isEmpty()) {
            loadFiles(pathStack.pop(), callback);
        } else {
            callback.onShouldBackHome();
        }
    }

    private List<FileEntity> sortByType(List<FileEntity> fileEntities) {
        List<FileEntity> result = new ArrayList<>();
        List<FileEntity> folderEntities = new ArrayList<>();
        List<FileEntity> audioEntities = new ArrayList<>();

        for (FileEntity fileEntity : fileEntities) {
            if (fileEntity.getFileType() == FileType.TYPE_FOLDER) {
                folderEntities.add(fileEntity);
            } else if (fileEntity.getFileType() == FileType.TYPE_AUDIO) {
                audioEntities.add(fileEntity);
            }
        }

        result.addAll(folderEntities);
        result.addAll(audioEntities);
        return result;
    }
}
