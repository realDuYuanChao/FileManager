package com.github.shellhub.filemanager.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.shellhub.filemanager.R;
import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.entity.FileInsertEvent;
import com.github.shellhub.filemanager.entity.FileRemoveEvent;
import com.github.shellhub.filemanager.entity.ScrollEvent;
import com.github.shellhub.filemanager.entity.ShowModeEvent;
import com.github.shellhub.filemanager.event.FileActionEvent;
import com.github.shellhub.filemanager.event.FileEntitiesEvent;
import com.github.shellhub.filemanager.event.FileEntityEvent;
import com.github.shellhub.filemanager.event.RenameEvent;
import com.github.shellhub.filemanager.fragment.HomeFragment;
import com.github.shellhub.filemanager.presenter.MainPresenter;
import com.github.shellhub.filemanager.presenter.impl.MainPresenterImpl;
import com.github.shellhub.filemanager.utils.AppUtils;
import com.github.shellhub.filemanager.view.MainView;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity
        implements MainView, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_action_menu)
    FloatingActionsMenu fabActionMenu;

    @BindView(R.id.fab_create_folder_action)
    FloatingActionButton fabCreateFolderAction;

    @BindView(R.id.fab_create_file_action)
    FloatingActionButton fabCreateFileAction;


    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private String TAG = this.getClass().getSimpleName();
    private MainPresenter mainPresenter;

    private boolean isGrid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //request permission
        MainActivityPermissionsDispatcher.initHomeWithPermissionCheck(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        mainPresenter.loadParent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bulleted:
                if (isGrid) {
                    item.setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
                } else {
                    item.setIcon(R.drawable.ic_view_module_white_24dp);
                }
                isGrid = !isGrid;
                EventBus.getDefault().post(new ShowModeEvent());
                break;
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.fab_create_folder_action, R.id.fab_create_file_action})
    public void onClick(View view) {
        String title = "";
        switch (view.getId()) {
            case R.id.fab_create_folder_action:
                title = getResources().getString(R.string.new_folder);
                break;
            case R.id.fab_create_file_action:
                title = getResources().getString(R.string.new_file);
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(this);
        input.setText("");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(AppUtils.getApp().getResources().getString(R.string.ok),
                (dialog, which) -> {
                    mainPresenter.createFolder(input.getText().toString());
                }).setNegativeButton(AppUtils.getApp().getResources().getString(R.string.cancel),
                (dialog, which) -> dialog.cancel()).show();


        fabActionMenu.collapse();
    }

    @Override
    public void showFiles(List<FileEntity> fileEntities) {
        EventBus.getDefault().post(new FileEntitiesEvent(fileEntities));
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void rename(RenameEvent renameEvent) {
        EventBus.getDefault().post(renameEvent);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @Override
    public void initHome() {
        mainPresenter.loadFiles("/sdcard/");
    }

    @Override
    public void playAudio(String audioPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(audioPath));
        intent.setDataAndType(photoURI, "audio/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void delete(int position) {
        EventBus.getDefault().post(new FileRemoveEvent(position));
    }

    @Override
    public void hideCreateButton() {
        fabActionMenu.setVisibility(View.GONE);
    }

    @Override
    public void showCreateButton() {
        fabActionMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public void addFileAt(int position, FileEntity fileEntity) {
        EventBus.getDefault().post(new FileInsertEvent(position, fileEntity));
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void permissionRequest() {

    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationForStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_storage_ration)
                .setNegativeButton(R.string.ok, (dialog, which) -> request.proceed())
                .setPositiveButton(R.string.cancel, (dialog, which) -> dialog.cancel()).show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForStorage() {
        //TODO
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForStorage() {
        //TODO
        Toast.makeText(this, "you have disable permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void setUpMVP() {
        mainPresenter = new MainPresenterImpl(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeFragment()).commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileEntityEvent(FileEntityEvent entityEvent) {
        mainPresenter.loadFiles(entityEvent.getFileEntity().getPath());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFileActionEvent(FileActionEvent fileActionEvent) {
        mainPresenter.handleFileAction(fileActionEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScrollEvent(ScrollEvent event) {
        mainPresenter.handleScrollEvent(event);
    }

}
