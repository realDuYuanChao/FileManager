package com.github.shellhub.filemanager.activity;

import android.os.Bundle;

import com.github.shellhub.filemanager.view.BaseView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements BaseView{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpMVP();
    }

    @Override
    public void setUpMVP() {

    }
}
