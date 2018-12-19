package com.github.shellhub.filemanager.adapter;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.shellhub.filemanager.R;
import com.github.shellhub.filemanager.entity.FileAction;
import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.entity.FileType;
import com.github.shellhub.filemanager.event.FileActionEvent;
import com.github.shellhub.filemanager.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FileEntity> fileEntities = new ArrayList<>();
    private Context mContext;

    private final int TYPE_FOLDER = 0;
    private final int TYPE_AUDIO = 1;
    private final int TYPE_IMAGE = 2;
    private final int TYPE_TXT = 3;
    private final int TYPE_GRID = 4;

    private boolean isGrid = false;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        switch (viewType) {
            case TYPE_FOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_home_folder_item, parent, false);
                ButterKnife.bind(this, view);
                return new HomeFolderViewHolder(view);
            case TYPE_AUDIO:
                view = LayoutInflater.from(mContext).inflate(R.layout.nav_home_audio_item, parent, false);
                ButterKnife.bind(this, view);
                return new HomeAudioViewHolder(view);
            case TYPE_IMAGE:
                //TODO
                break;
            case TYPE_TXT:
                view = LayoutInflater.from(mContext).inflate(R.layout.nav_home_txt_item, parent, false);
                ButterKnife.bind(this, view);
                return new HomeTXTViewHolder(view);
            case TYPE_GRID:
                view = LayoutInflater.from(mContext).inflate(R.layout.nav_home_grid_item, parent, false);
                ButterKnife.bind(this, view);
                return new HomeGridViewHolder(view);
            default:
                return null;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HomeFolderViewHolder) {
            ((HomeFolderViewHolder) holder).bind(position);
        } else if (holder instanceof HomeAudioViewHolder) {
            ((HomeAudioViewHolder) holder).bind(position);
        } else if (holder instanceof HomeTXTViewHolder) {
            ((HomeTXTViewHolder) holder).bind(position);
        } else if (holder instanceof HomeGridViewHolder) {
            ((HomeGridViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isGrid) {
            return TYPE_GRID;
        }
        FileEntity fileEntity = fileEntities.get(position);
        if (fileEntity.getFileType() == FileType.TYPE_FOLDER) {
            return TYPE_FOLDER;
        } else if (fileEntity.getFileType() == FileType.TYPE_AUDIO) {
            return TYPE_AUDIO;
        } else if (fileEntity.getFileType() == FileType.TYPE_TEXT) {
            return TYPE_TXT;
        } else {
            return TYPE_FOLDER;
        }
    }

    @Override
    public int getItemCount() {
        return fileEntities.size();
    }


    public class HomeFolderViewHolder extends RecyclerView.ViewHolder {
        private String TAG = this.getClass().getSimpleName();
        @BindView(R.id.tvHomeAudioName)
        TextView tvHomeFolderName;

        @BindView(R.id.tv_home_folder_sub_count)
        TextView tvHomeFolderSubCount;

        @BindView(R.id.tv_folder_last_modify_time)
        TextView tvFolderLastModifyTime;


        @BindView(R.id.iv_home_audio_more_menu)
        ImageView ivHomeFolderMoreMenu;

        @BindView(R.id.iv_home_folder)
        ImageView ivHomeFolder;

        public HomeFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            FileEntity fileEntity = fileEntities.get(position);
            tvHomeFolderName.setText(fileEntity.getName());
            tvFolderLastModifyTime.setText(fileEntity.getFormatLastModify());
            tvHomeFolderSubCount.setText(fileEntity.getSubCountTitle());
            showPopMenu(ivHomeFolderMoreMenu, fileEntity, position);
            itemView.setOnClickListener((view) -> {
                EventBus.getDefault().post(new FileActionEvent(fileEntity, FileAction.ACTION_OPEN, position));
            });
        }
    }


    public class HomeAudioViewHolder extends RecyclerView.ViewHolder {

        private String TAG = this.getClass().getSimpleName();
        @BindView(R.id.tvHomeAudioName)
        TextView tvHomeAudioName;

        @BindView(R.id.iv_home_audio_more_menu)
        ImageView ivHomeAudioMoreMenu;

        @BindView(R.id.iv_audio_album_cover)
        ImageView ivAudioAlbumCover;

        @BindView(R.id.tv_album_name)
        TextView tvAlbumName;

        @BindView(R.id.tv_home_audio_duration)
        TextView tvHomeAudioDuration;

        public HomeAudioViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            FileEntity fileEntity = fileEntities.get(position);
            tvHomeAudioName.setText(fileEntity.getName());
            tvAlbumName.setText(fileEntity.getAlbumName());
            Glide.with(mContext).load(fileEntity.getEmbeddedPicture()).into(ivAudioAlbumCover);
            tvHomeAudioDuration.setText(fileEntity.getDuration());
            showPopMenu(ivHomeAudioMoreMenu, fileEntity, position);
            itemView.setOnClickListener((view) -> {
                EventBus.getDefault().post(new FileActionEvent(fileEntity, FileAction.ACTION_OPEN, position));
            });
        }
    }

    public class HomeTXTViewHolder extends RecyclerView.ViewHolder {
        private String TAG = this.getClass().getSimpleName();
        @BindView(R.id.tv_home_txt_name)
        TextView tvHomeTXTName;

        @BindView(R.id.tv_home_txt_size)
        TextView tvHomeTXTSize;

        @BindView(R.id.tv_txt_last_modify_time)
        TextView tvTXTLastModifyTime;


        @BindView(R.id.iv_home_txt_more_menu)
        ImageView ivHomeTXTMoreMenu;

        @BindView(R.id.iv_home_txt_icon)
        ImageView ivHomeTXTIcon;

        public HomeTXTViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            FileEntity fileEntity = fileEntities.get(position);
            tvHomeTXTName.setText(fileEntity.getName());
            tvTXTLastModifyTime.setText(fileEntity.getFormatLastModify());
            tvHomeTXTSize.setText(fileEntity.getFormatSize());
            showPopMenu(ivHomeTXTMoreMenu, fileEntity, position);
            itemView.setOnClickListener((view) -> {
                EventBus.getDefault().post(new FileActionEvent(fileEntity, FileAction.ACTION_OPEN, position));
            });
        }
    }

    public class HomeGridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_home_grid_name)
        TextView tvHomeGridName;

        @BindView(R.id.iv_home_grid_more)
        ImageView ivHomeGridMore;

        @BindView(R.id.iv_home_grid_pic)
        ImageView ivHomeGridPic;


        public HomeGridViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            FileEntity fileEntity = fileEntities.get(position);
            tvHomeGridName.setText(fileEntity.getName());

            switch (fileEntity.getFileType()) {
                case TYPE_FOLDER:
                    Glide.with(mContext).load(R.drawable.ic_folder_black_24dp).into(ivHomeGridPic);
                    break;
                case TYPE_AUDIO:
                    Glide.with(mContext).load(fileEntity.getEmbeddedPicture()).into(ivHomeGridPic);
                    break;
                case TYPE_TEXT:
                    Glide.with(mContext).load(R.drawable.ic_txt_type).into(ivHomeGridPic);
                    break;
            }

            showPopMenu(ivHomeGridMore, fileEntity, position);
            itemView.setOnClickListener((view) -> {
                EventBus.getDefault().post(new FileActionEvent(fileEntity, FileAction.ACTION_OPEN, position));
            });
        }
    }

    private void showPopMenu(View overview, FileEntity fileEntity, int position) {
        overview.setOnClickListener((view) -> {
            PopupMenu menu = new PopupMenu(mContext, view);
            menu.inflate(R.menu.pop);
            MenuPopupHelper menuHelper = new MenuPopupHelper(mContext, (MenuBuilder) menu.getMenu(), view);
            menuHelper.setForceShowIcon(true);
            menuHelper.show();
            menu.setOnMenuItemClickListener(item -> {
                final FileActionEvent fileActionEvent = new FileActionEvent(fileEntity, position);
                switch (item.getItemId()) {
                    case R.id.open:
                        fileActionEvent.setFileAction(FileAction.ACTION_OPEN);
                        break;
                    case R.id.select:
                        //TODO
                        break;
                    case R.id.select_all:
                        //TODO
                        break;
                    case R.id.rename:
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(AppUtils.getApp().getResources().getString(R.string.rename));

                        // Set up the input
                        final EditText input = new EditText(mContext);
                        input.setText("");
                        input.append(fileEntity.getName());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton(AppUtils.getApp().getResources().getString(R.string.ok),
                                (dialog, which) -> {
                                    String newName = input.getText().toString();
                                    fileActionEvent.setFileAction(FileAction.ACTION_RENAME);
                                    fileActionEvent.getFileEntity().setNewName(newName);
                                    EventBus.getDefault().post(fileActionEvent);
                                }).setNegativeButton(AppUtils.getApp().getResources().getString(R.string.cancel),
                                (dialog, which) -> dialog.cancel()).show();
                        break;
                    case R.id.delete:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext)
                                .setTitle(AppUtils.getApp().getResources().getString(R.string.delete_dialog_title))
                                .setMessage(fileEntity.getName())
                                .setPositiveButton(AppUtils.getApp().getResources().getString(R.string.ok), (dialog, which) -> {
                                    fileActionEvent.setFileAction(FileAction.ACTION_DELETE);
                                    EventBus.getDefault().post(fileActionEvent);
                                }).setNegativeButton(AppUtils.getApp().getResources().getString(R.string.cancel), null);
                        alertDialog.create().show();
                        break;
                    case R.id.copy:
                        fileActionEvent.setFileAction(FileAction.ACTION_COPY);
                        //todo
                        break;
                    case R.id.cut:
                        fileActionEvent.setFileAction(FileAction.ACTION_CUT);
                        break;
                    case R.id.properties:
                        View propertiesView = LayoutInflater.from(mContext).inflate(R.layout.layout_file_properties, null);

                        TextView tvNameContent = propertiesView.findViewById(R.id.tv_name_content);
                        TextView tvLocationContent = propertiesView.findViewById(R.id.tv_location_content);
                        TextView tvFileSizeContent = propertiesView.findViewById(R.id.tv_file_size_content);
                        TextView tvTypeContent = propertiesView.findViewById(R.id.tv_type_content);
                        TextView tvModifiedContent = propertiesView.findViewById(R.id.tv_modified_date_content);

                        tvNameContent.setText(fileEntity.getName());
                        tvLocationContent.setText(fileEntity.getPath());
                        tvFileSizeContent.setText(fileEntity.getFormatSize());
                        tvTypeContent.setText(fileEntity.getFormat());
                        tvModifiedContent.setText(fileEntity.getFormatLastModify());

                        alertDialog = new AlertDialog.Builder(mContext)
                                .setView(propertiesView)
                                .setPositiveButton(R.string.ok, null);
                        alertDialog.create().show();
                        break;
                    default:
                        break;
                }
                EventBus.getDefault().post(fileActionEvent);
                return true;
            });
        });
    }
}
