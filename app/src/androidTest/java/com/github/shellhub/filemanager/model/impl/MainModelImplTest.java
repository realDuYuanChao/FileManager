package com.github.shellhub.filemanager.model.impl;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.runner.AndroidJUnit4;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainModelImplTest {

    @Test
    public void loadFiles() {
        new MainModelImpl().loadFiles("/sdcard/", System.out::println);
    }
}