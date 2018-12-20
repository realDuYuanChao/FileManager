package com.github.shellhub.filemanager.model.impl;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.github.shellhub.filemanager.R;
import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.entity.FileType;
import com.github.shellhub.filemanager.event.FileActionEvent;
import com.github.shellhub.filemanager.event.RenameEvent;
import com.github.shellhub.filemanager.model.MainModel;
import com.github.shellhub.filemanager.utils.AppUtils;
import com.github.shellhub.filemanager.utils.FileUtils;
import com.github.shellhub.filemanager.utils.TimeUtils;

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

                //init modified time
                fileEntity.setLastMidify(files[i].lastModified());
                fileEntity.setFormatLastModify(TimeUtils.formatDate(fileEntity.getLastMidify()));

                //init size
                fileEntity.setSize(files[i].length());
                fileEntity.setFormatSize(FileUtils.formatFileSize(fileEntity.getSize()));

                //init extension
                fileEntity.setFormat(FileUtils.getExtension(fileEntity.getName()));

                FileType fileType = FileUtils.getFileType(files[i]);
                if (fileType == FileType.TYPE_FOLDER) {
                    fileEntity.setFormat(AppUtils.getApp().getResources().getString(R.string.folder));
                    fileEntity.setSubCount(files[i].listFiles().length);
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
                } else if (fileType == FileType.TYPE_IMAGE) {
                } else if (fileType == FileType.TYPE_TEXT) {
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

    @Override
    public void handleFileAction(FileActionEvent fileActionEvent, Callback callback) {
        FileEntity fileEntity = fileActionEvent.getFileEntity();
        int position = fileActionEvent.getPosition();
        switch (fileActionEvent.getFileAction()) {
            case ACTION_OPEN:
                switch (fileEntity.getFileType()) {
                    case TYPE_FOLDER:
                        loadFiles(fileEntity.getPath(), callback);
                        break;
                    case TYPE_AUDIO:
                        String audioPath = fileActionEvent.getFileEntity().getPath();
                        if (audioPath != null) callback.onAudioLoad(audioPath);
                        break;
                    case TYPE_VIDEO:
                        //todo
                        break;
                    case TYPE_IMAGE:
                        break;
                    case TYPE_PDF:
                        break;
                    case TYPE_TEXT:
                        break;
                    case TYPE_UNKNOWN:
                }
                break;
            case ACTION_RENAME:
                //rename file
                fileEntity = fileActionEvent.getFileEntity();
                String newName = fileEntity.getNewName();
                String name = fileEntity.getName();

                String path = fileEntity.getPath();
                String newPath = path.replace(name, newName);

                Log.d(TAG, "handleFileAction: " + path + "->" + newPath);
                boolean renamed = new File(path).renameTo(new File(newPath));

                if (renamed) {
                    fileEntity.setName(newName);
                    fileEntity.setNewName(newName);
                    fileEntity.setPath(newPath);
                    fileEntity.setNewPath(newPath);
                    callback.onRenameCompleted(new RenameEvent(fileEntity, fileActionEvent.getPosition()));
                } else {
                    Log.e(TAG, "handleFileAction: renamed file " + path + "to " + newPath + "failed");
                }
                break;
            case ACTION_DELETE:
                boolean deleted = new File(fileEntity.getPath()).delete();
                if (deleted) callback.onDeleted(position);
                break;
            case ACTION_COPY:
                //copy file
                break;
            case ACTION_CUT:
                //cut file
                break;
        }
    }

    @Override
    public void createFolder(String name, Callback callback) {
        File newFile = new File(pathStack.peek(), name);
        boolean created = newFile.mkdir();
        if (created) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setSize(newFile.length());
            fileEntity.setFormatSize(FileUtils.formatFileSize(fileEntity.getSize()));
            fileEntity.setPath(newFile.getPath());
            fileEntity.setName(newFile.getName());
            fileEntity.setLastMidify(newFile.lastModified());
            fileEntity.setFormatLastModify(TimeUtils.formatDate(fileEntity.getLastMidify()));
            fileEntity.setSubCount(0);
            fileEntity.setSubCountTitle("(" + fileEntity.getSubCount() + " " + AppUtils.getApp().getResources().getString(R.string.item) + ")");//e.g(1 item));
            callback.onFolderCreated(fileEntity);
        }
    }

    @Override
    public void createFile(String name, Callback callback) {

    }

    private List<FileEntity> sortByType(List<FileEntity> fileEntities) {
        List<FileEntity> result = new ArrayList<>();
        List<FileEntity> folderEntities = new ArrayList<>();
        List<FileEntity> audioEntities = new ArrayList<>();
        List<FileEntity> txtEntities = new ArrayList<>();

        for (FileEntity fileEntity : fileEntities) {
            if (fileEntity.getFileType() == FileType.TYPE_FOLDER) {
                folderEntities.add(fileEntity);
            } else if (fileEntity.getFileType() == FileType.TYPE_AUDIO) {
                audioEntities.add(fileEntity);
            } else if (fileEntity.getFileType() == FileType.TYPE_TEXT) {
                txtEntities.add(fileEntity);
            }
        }

        result.addAll(folderEntities);
        result.addAll(audioEntities);
        result.addAll(txtEntities);
        return result;
    }
}
