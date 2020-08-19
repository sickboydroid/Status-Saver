package com.gameofcoding.statussaver.utils;

import android.os.Environment;
import java.io.File;

public abstract class AppConstants {
    /////////////////////////
    // Package name of app //
    /////////////////////////
    public static final String PACKAGE_NAME = "com.gameofcoding.statussaver";

    ///////////////////////////////////////
    // Constants for controlling logging //
    ///////////////////////////////////////
    public static final boolean DEBUG = true;
    public static final boolean EXTREME_LOGGING = false;

    //////////////////////////
    // File names and paths //
    //////////////////////////
    @SuppressWarnings("deprecation")
    public static final File EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory();
    public static final File WHATSAPP_STATUS_DIRECTORY = new File(EXTERNAL_STORAGE_DIR, "/WhatsApp/Media/.Statuses");
    
    /////////////////////
    // Preference keys //
    /////////////////////
    public static final class preference {
	private preference() {}
    }
}
