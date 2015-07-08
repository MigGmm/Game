package com.game.miguel.game.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.game.miguel.game.model.Score;

import java.util.ArrayList;

/**
 * Class for control the SQLite database.
 */
public class ControlSQLite extends SQLiteOpenHelper {

    private static final String TABLE = "scores";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SCORE = "score";
    private static final String[] COLUMNS = {ID, NAME, SCORE};
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE + " (" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " VARCHAR," + SCORE + " INTEGER);";
    private ArrayList<Score> puntuaciones;

    public ControlSQLite(Context context) {
        super(context, TABLE, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + TABLE);
        onCreate(db);
    }

    /**
     * Insert the score object into the database.
     * @param score
     * @return
     */
    public long insertScore(Score score) {
        long respuesta = -1;
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            ContentValues cv = new ContentValues();
            if (cv != null) {
                cv.putNull(ID);
                cv.put(NAME, score.getNombre());
                cv.put(SCORE, score.getScore());
                respuesta = db.insert(TABLE, null, cv);
            }
        }
        db.close();
        return respuesta;
    }

    /**
     * Return an ArrayList with the scores from database.
     * @return
     */
    public ArrayList<Score> listScores() {
        puntuaciones = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, COLUMNS, null, null, null, null, SCORE + " DESC");
        if (cursor.moveToFirst()){
            do {
                Score score = new Score();
                score.setNombre(cursor.getString(1));
                score.setScore(cursor.getInt(2));
                puntuaciones.add(score);
            } while (cursor.moveToNext());
        }
        db.close();
        return puntuaciones;
    }
}
