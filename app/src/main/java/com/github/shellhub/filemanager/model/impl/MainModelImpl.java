package com.github.shellhub.filemanager.model.impl;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.github.shellhub.filemanager.R;
import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.entity.FileType;
import com.github.shellhub.filemanager.model.MainModel;
import com.github.shellhub.filemanager.utils.AppUtils;
import com.github.shellhub.filemanager.utils.FileUtils;
import com.github.shellhub.filemanager.utils.TimeUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainModelImpl implements MainModel {

    private String TAG = this.getClass().getSimpleName();
    private static Stack<String> pathStack = new Stack<>();
    private boolean shouldPush = true;

    static {
        pathStack.add("/sdcard/");
    }

    @Override
    public void loadFiles(String rootPath, Callback callback) {
        File file = new File(rootPath);
        if (!file.isDirectory()) {
            return;
        }

        if (shouldPush) {
            pathStack.add(rootPath);
            shouldPush = true;
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

                FileType fileType = FileUtils.getFileType(files[i]);
                if (fileType == FileType.TYPE_FOLDER) {
                    fileEntity.setSubCount(files[i].listFiles().length);
                    fileEntity.setLastMidify(files[i].lastModified());

                    fileEntity.setFormatLastModify(new SimpleDateFormat("MM/d/YY,hh:mm a", Locale.ENGLISH)
                            .format(new Date(fileEntity.getLastMidify())));

                    int subCount = files[i].listFiles().length;
                    String subCountTitle;
                    if (files[i].listFiles().length > 1) {
                        subCountTitle = "(" + subCount + " " + AppUtils.getApp().getResources().getString(R.string.items) + ")";//e.g(2 items)
                    } else {
                        subCountTitle = "(" + subCount + " " + AppUtils.getApp().getResources().getString(R.string.item) + ")";//e.g(1 item)
                    }
                    fileEntity.setSubCountTitle(subCountTitle);


                } else if (fileType == FileType.TYPE_AUDIO) {
                    //extra meta tools
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(fileEntity.getPath());
                    fileEntity.setEmbeddedPicture(mmr.getEmbeddedPicture());
                    fileEntity.setAlbumName(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                    fileEntity.setDuration(TimeUtils.formatDuration(Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))));
                } else if (fileType == FileType.TYPE_PDF) {
                    //todo
                } else if (fileType == FileType.TYPE_IMAGE) {
                    //todo
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
            }
        });
    }

    @Override
    public void loadParent(Callback callback) {
        shouldPush = false;
        if (pathStack.size() >= 2) {
            pathStack.pop();
            loadFiles(pathStack.peek(), callback);
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
