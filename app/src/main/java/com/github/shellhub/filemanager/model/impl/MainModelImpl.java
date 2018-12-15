package com.github.shellhub.filemanager.model.impl;

import android.util.Log;

import com.github.shellhub.filemanager.entity.FileEntity;
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

        if (shouldPush) {
            pathStack.push(file.getParent());
        }

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
                callback.onLoadFiles(fileEntities);
            }
        });
    }

    @Override
    public void loadParent(Callback callback) {
        Log.d(TAG, "loadParent: " + pathStack);
        shouldPush = false;
        if (!pathStack.isEmpty()) {
            loadFiles(pathStack.pop(), callback);
        } else {
            callback.onShouldBackHome();
        }
    }
}
