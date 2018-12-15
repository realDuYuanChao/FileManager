package com.github.shellhub.filemanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.github.shellhub.filemanager.R;
import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.entity.FileType;
import com.github.shellhub.filemanager.event.FileEntityEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Setter;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Setter
    List<FileEntity> fileEntities = new ArrayList<>();
    private Context mContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_home_folder_item, parent, false);
        ButterKnife.bind(this, view);
        return new HomeFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HomeFolderViewHolder) {
            ((HomeFolderViewHolder) holder).bind(position);
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

        @BindView(R.id.iv_home_audio_more_menu)
        ImageView ivHomeFolderMoreMenu;

        public HomeFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position) {
            FileEntity fileEntity = fileEntities.get(position);
            tvHomeFolderName.setText(fileEntity.getName());
            String subCountTitle;
            if (fileEntity.getSubCount() > 1) {
                subCountTitle = "(" + fileEntities.get(position).getSubCount() + " " + mContext.getResources().getString(R.string.items) + ")";//e.g(2 items)
            } else {
                subCountTitle = "(" + fileEntities.get(position).getSubCount() + " " + mContext.getResources().getString(R.string.item) + ")";//e.g(1 item)
            }
            tvHomeFolderSubCount.setText(subCountTitle);

            ivHomeFolderMoreMenu.setOnClickListener((view) -> {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.pop, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.rename:
                            break;
                        case R.id.delete:
                            break;
                        case R.id.copy:
                            break;
                        case R.id.cut:
                            break;
                        default:
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });
            itemView.setOnClickListener((view) -> {
                EventBus.getDefault().post(new FileEntityEvent(fileEntities.get(position)));
            });
        }
    }
}
