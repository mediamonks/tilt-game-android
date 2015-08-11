package com.mediamonks.googleflip.data.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.mediamonks.googleflip.BuildConfig;
import com.mediamonks.googleflip.data.vo.LevelResultVO;
import com.mediamonks.googleflip.data.vo.LevelVO;

import nl.qbusict.cupboard.CupboardBuilder;
import nl.qbusict.cupboard.CupboardFactory;
import temple.core.database.EmptyCursor;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Data provider, storing levels and results
 */
public class DataProvider extends ContentProvider {
    private static final String TAG = DataProvider.class.getSimpleName();

    public static final String PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    public static final String BASE_LEVEL = "level";
    public static final String BASE_LEVEL_RESULT = "levelresult";

    private static final int LEVEL_INDEX = 0;
    private static final int LEVEL_RESULT_INDEX = 1;

    private static final Object LOCK = new Object();
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        CupboardBuilder instance = new CupboardBuilder();
        instance.useAnnotations();
        CupboardFactory.setCupboard(instance.build());

        cupboard().register(LevelVO.class);
        cupboard().register(LevelResultVO.class);

        sMatcher.addURI(PROVIDER_AUTHORITY, BASE_LEVEL, LEVEL_INDEX);
        sMatcher.addURI(PROVIDER_AUTHORITY, BASE_LEVEL_RESULT, LEVEL_RESULT_INDEX);
    }

    private DatabaseHelper _databaseHelper;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (LOCK) {
            try {
                final SQLiteDatabase db = getDatabaseHelper().getReadableDatabase();
                Class<?> tableClass = getTableClass(uri);

                Cursor cursor = cupboard().withDatabase(db)
                        .query(tableClass)
                        .withProjection(projection)
                        .withSelection(selection, selectionArgs)
                        .orderBy(sortOrder)
                        .getCursor();
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                return cursor;
            } catch (Exception error) {
                EmptyCursor cursor = new EmptyCursor();
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (LOCK) {
            final SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
            final Class<?> tableClass = getTableClass(uri);

            long id = db.insertWithOnConflict(cupboard().withEntity(tableClass).getTable(), null, values, SQLiteDatabase.CONFLICT_REPLACE);

            notifyChange(uri);

            return ContentUris.withAppendedId(uri, id);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (LOCK) {
            final SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();
            final Class<?> tableClass = getTableClass(uri);

            final int rowsDeleted = cupboard().withDatabase(db).delete(tableClass, selection, selectionArgs);

            notifyChange(uri);

            return rowsDeleted;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (LOCK) {
            final SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();

            final Class<?> tableClass = getTableClass(uri);

            final int rowsUpdated = cupboard().withDatabase(db).update(tableClass, values, selection, selectionArgs);

            notifyChange(uri);

            return rowsUpdated;
        }
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        synchronized (LOCK) {
            SQLiteDatabase db = getDatabaseHelper().getWritableDatabase();

            final String table = getTableString(uri);

            db.beginTransaction();

            int rowsInserted = 0;
            try {
                for (ContentValues value : values) {
                    db.insertWithOnConflict(table, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                    rowsInserted++;
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();

                rowsInserted = -1;
            } finally {
                db.endTransaction();

                if (rowsInserted > 0) {
                    notifyChange(uri);
                }
            }

            return rowsInserted;
        }
    }

    private DatabaseHelper getDatabaseHelper() {
        if (_databaseHelper == null) {
            _databaseHelper = new DatabaseHelper(getContext());
        }
        return _databaseHelper;
    }

    private Class<?> getTableClass(Uri uri) {
        switch (sMatcher.match(uri)) {
            case LEVEL_INDEX:
                return LevelVO.class;
            case LEVEL_RESULT_INDEX:
                return LevelResultVO.class;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private void notifyChange(final Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    private String getTableString(Uri uri) {
        return cupboard().getTable(getTableClass(uri));
    }
}
