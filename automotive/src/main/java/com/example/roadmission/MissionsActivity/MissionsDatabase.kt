package com.example.roadmission
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MissionsDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private var DATABASE_VERSION: Int = 1
        private var DATABASE_NAME: String = "Missions_Database"
        private var TABLE_1: String = "One_Person_Table"
        private var TABLE_2: String = "Two_Person_Table"
        private var TABLE_3: String = "Three_Person_Table"
        private var ID: String = "id"
        private var MISSION_TEXT: String = "mission"
        private var CREATE_TABLE_1: String =
            "CREATE TABLE " + TABLE_1 + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MISSION_TEXT +" TEXT UNIQUE" + ")"
        private var CREATE_TABLE_2: String =
            "CREATE TABLE " + TABLE_2 + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MISSION_TEXT +" TEXT UNIQUE" + ")"
        private var CREATE_TABLE_3: String =
            "CREATE TABLE " + TABLE_3 + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + MISSION_TEXT +" TEXT UNIQUE" + ")"
        private var DELETE_TABLE_1: String = "DROP TABLE IF EXISTS " + TABLE_1
        private var DELETE_TABLE_2: String = "DROP TABLE IF EXISTS " + TABLE_2
        private var DELETE_TABLE_3: String = "DROP TABLE IF EXISTS " + TABLE_3
    }

    fun createMissions(){
        val mission1_T1 = "1Do the flop for 1"
        val mission1_T2 = "1Do the double flop for 2"
        val mission1_T3 = "1Do the triple flop for 3"
        createMission(TABLE_1,mission1_T1)
        createMission(TABLE_2,mission1_T2)
        createMission(TABLE_3,mission1_T3)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_1)
        db?.execSQL(CREATE_TABLE_2)
        db?.execSQL(CREATE_TABLE_3)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DELETE_TABLE_1)
        db?.execSQL(DELETE_TABLE_2)
        db?.execSQL(DELETE_TABLE_3)
        onCreate(db)
    }

    fun createMission(table:String , mission:String)
    {
        var db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(MISSION_TEXT, mission);
        db.insert(table, null ,contentValues);
    }



}
