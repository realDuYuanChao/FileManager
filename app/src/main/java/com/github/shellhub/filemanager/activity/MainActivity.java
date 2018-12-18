package com.github.shellhub.filemanager.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.shellhub.filemanager.R;
import com.github.shellhub.filemanager.entity.FileEntity;
import com.github.shellhub.filemanager.event.FileActionEvent;
import com.github.shellhub.filemanager.event.FileEntitiesEvent;
import com.github.shellhub.filemanager.event.FileEntityEvent;
import com.github.shellhub.filemanager.event.RenameEvent;
import com.github.shellhub.filemanager.fragment.HomeFragment;
import com.github.shellhub.filemanager.presenter.MainPresenter;
import com.github.shellhub.filemanager.presenter.impl.MainPresenterImpl;
import com.github.shellhub.filemanager.view.MainView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements MainView, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private String TAG = this.getClass().getSimpleName();
    private MainPresenter mainPresenter;

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
        mainPresenter.loadFiles("/sdcard/");

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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}
