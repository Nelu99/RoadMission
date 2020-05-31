package com.example.roadmission
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProgressDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private var DATABASE_VERSION: Int = 1
        private var DATABASE_NAME: String = "ACHIEVEMENTS_DATABASE"
        private var TABLE_NAME: String = "TABLE_PROGRESS"
        private var COL_1: String = "MISSION"
        private var COL_2: String = "DIFFICULTY"
        private var COL_3: String = "COMPLETIONS"
        private var COL_4: String = "DATE"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(
            "CREATE TABLE $TABLE_NAME (MISSION TEXT PRIMARY KEY," +
                    "MISSION TEXT,DIFFICULTY TEXT,COMPLETIONS INTEGER,DATE TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addAchievement(mission: String, difficulty: String, completions: Int, date: String): Boolean {
        var db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(COL_1, mission)
        contentValues.put(COL_2, difficulty)
        contentValues.put(COL_3, completions)
        contentValues.put(COL_4, date)
        var result = db.insert(TABLE_NAME, null ,contentValues)
        return when(result.toInt()) {
            -1 -> false
            else -> true
        }
    }

    fun updateAchievement(mission: String, difficulty: String, completions: Int, date: String): Boolean {
        var db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(COL_1, mission)
        contentValues.put(COL_2, difficulty)
        contentValues.put(COL_3, completions)
        contentValues.put(COL_4, date)
        db.update(TABLE_NAME, contentValues, "MISSION = ?", Array(1){mission});
        return true
    }

    fun isAchievementRegistered(mission: String): Boolean {
        val db = writableDatabase
        val query = "select * from day_table where ID = '$mission'"
        val res = db.rawQuery(query, null)
        if (res.count <= 0) {
            res.close()
            return false
        }
        res.close()
        return true
    }

    fun getAllAchievements(): Cursor? {
        val db = this.writableDatabase
        return db.rawQuery("select * from $TABLE_NAME", null)
    }

    fun getAchievementByDate(date: String): Cursor? {
        val db = this.writableDatabase
        return db.rawQuery("select * from $TABLE_NAME " +
                " where DATE = '$date'", null)
    }

}
