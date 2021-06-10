package com.livewallpapers.huawei.data.utils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.livewallpapers.huawei.data.adapter.AdapterWallpaper;
import com.livewallpapers.huawei.data.model.ModelWallpaper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BSWALLPAPERV2";
    private static final String TABLE_NAME = "Favorite";
    private static final String KEY_ID = "id";
    private static final String KEY_WALL_ID = "wall_id";
    private static final String KEY_CAT_ID = "cat_id";
    private static final String KEY_TITLE = "wallpaper_title";
    private static final String KEY_IMAGE_THUMB = "wallpaper_image_thumb";
    private static final String KEY_IMAGE_DETAIL = "wallpaper_image_detail";
    private static final String KEY_IMAGE = "wallpaper_image_original";
    private static final String KEY_TAGS = "wallpaper_tags";
    private static final String KEY_TYPE = "wallpaper_type";
    private static final String KEY_PREMIUM = "wallpaper_premium";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_WALL_ID + " TEXT,"
                + KEY_CAT_ID + " TEXT,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE_THUMB + " TEXT,"
                + KEY_IMAGE_DETAIL + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_TAGS + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_PREMIUM + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void AddtoFavorite(ModelWallpaper modelWallpaper) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WALL_ID, modelWallpaper.wall_id);
        values.put(KEY_CAT_ID, modelWallpaper.cat_id);
        values.put(KEY_TITLE, modelWallpaper.wallpaper_title);
        values.put(KEY_IMAGE_THUMB, modelWallpaper.wallpaper_image_thumb);
        values.put(KEY_IMAGE_DETAIL, modelWallpaper.wallpaper_image_detail);
        values.put(KEY_IMAGE, modelWallpaper.wallpaper_image_original);
        values.put(KEY_TAGS, modelWallpaper.wallpaper_tags);
        values.put(KEY_TYPE, modelWallpaper.wallpaper_type);
        values.put(KEY_PREMIUM, modelWallpaper.wallpaper_premium);
        db.insert(TABLE_NAME, null, values);
        db.close();

    }

    public ArrayList<ModelWallpaper> getFavouriteWallpapers() {
        ArrayList<ModelWallpaper> arrayList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                int wall_id = cursor.getInt(cursor.getColumnIndex(KEY_WALL_ID));
                int cat_id = cursor.getInt(cursor.getColumnIndex(KEY_CAT_ID));
                String wallpaper_title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String wallpaper_image_thumb = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_THUMB));
                String wallpaper_image_detail = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_DETAIL));
                String wallpaper_image_original = cursor.getString(cursor.getColumnIndex(KEY_IMAGE));
                String wallpaper_tags = cursor.getString(cursor.getColumnIndex(KEY_TAGS));
                String wallpaper_type = cursor.getString(cursor.getColumnIndex(KEY_TYPE));
                String wallpaper_premium = cursor.getString(cursor.getColumnIndex(KEY_PREMIUM));
                arrayList.add(new ModelWallpaper(wall_id, cat_id, wallpaper_title, wallpaper_image_thumb, wallpaper_image_detail, wallpaper_image_original,
                        wallpaper_tags, wallpaper_type, wallpaper_premium, AdapterWallpaper.VIEW_ITEM));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    public void RemoveFav(int wall_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_WALL_ID + " = ?", new String[]{String.valueOf(wall_id)});
        db.close();
    }

    public Boolean isFav(int wall_id) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME +" WHERE wall_id = " + wall_id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
