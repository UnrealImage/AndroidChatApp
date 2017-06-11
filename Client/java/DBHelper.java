package aki.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Shadow on 2016/12/13.
 */
public class DBHelper extends SQLiteOpenHelper {
    // database store the contacts and history
    private static final String DB_NAME = "ContactAndDialog.db";
    private static final int DB_VERSION = 1;
    private static String CONTACT_TABLE;
    private static String DIALOG_TABLE;
    private static String ADDCONTACT_TABLE;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        CONTACT_TABLE = "ContactTable" + Contact.userId;
        DIALOG_TABLE = "DialogTable" + Contact.userId;
        ADDCONTACT_TABLE = "AddContactTable" + Contact.userId;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        String CREATE_TABLE = "CREATE TABLE if not exists "
//                + CONTACT_TABLE
//                + "(id TEXT PRIMARY KEY,name TEXT,signature TEXT)";
//        db.execSQL(CREATE_TABLE);
//        CREATE_TABLE = "CREATE TABLE if not exists "
//                + DIALOG_TABLE
//                + "(messageId TEXT PRIMARY KEY,senderId TEXT,receiverId TEXT,content TEXT,time TEXT,isRead INTEGER)";
//        db.execSQL(CREATE_TABLE);
//        CREATE_TABLE = "CREATE TABLE if not exists "
//                + ADDCONTACT_TABLE
//                + "(id TEXT PRIMARY KEY,name TEXT,signature TEXT)";
//        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE if not exists "
                + CONTACT_TABLE
                + "(id TEXT PRIMARY KEY,name TEXT,signature TEXT)";
        db.execSQL(CREATE_TABLE);
        CREATE_TABLE = "CREATE TABLE if not exists "
                + DIALOG_TABLE
                + "(messageId TEXT PRIMARY KEY,senderId TEXT,receiverId TEXT,content TEXT,time TEXT,isRead INTEGER)";
        db.execSQL(CREATE_TABLE);
        CREATE_TABLE = "CREATE TABLE if not exists "
                + ADDCONTACT_TABLE
                + "(id TEXT PRIMARY KEY,name TEXT,signature TEXT)";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // contact table
    public void insertContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", contact.id);
        values.put("name", contact.name);
        values.put("signature", contact.signature);
        db.insert(CONTACT_TABLE, null, values);
        db.close();
    }
    public void updateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", contact.id);
        values.put("name", contact.name);
        values.put("signature", contact.signature);
        db.update(CONTACT_TABLE, values, "id = ?", new String[]{contact.id});
        db.close();
    }
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CONTACT_TABLE, "id=?", new String[]{contact.id});
        db.close();
    }
    public Contact queryContact(String id) {
        SQLiteDatabase db = getWritableDatabase();
        Contact result = null;
        Cursor cursor = db.rawQuery("select * from " + CONTACT_TABLE + " where id=?", new String[]{id});
        if (cursor != null && cursor.moveToNext()) {
            result = new Contact(
                    cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("signature")));
            cursor.close();
        }
        return result;
    }
    public ArrayList<Contact> getContactList() {
        ArrayList<Contact> result = new ArrayList<Contact>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CONTACT_TABLE, null);
        while (cursor.moveToNext()) {
            result.add(new Contact(
                    cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("signature"))));
        }
        return result;
    }
    
    // dialog table
    public void insertDialog(Dialog dialog) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("messageId", dialog.messageId);
        values.put("senderId", dialog.senderId);
        values.put("receiverId", dialog.receiverId);
        values.put("content", dialog.content);
        values.put("time", dialog.time);
        values.put("isRead", dialog.isRead);
        db.insert(DIALOG_TABLE, null, values);
        db.close();
    }
    public void updateDialog(Dialog dialog) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("messageId", dialog.messageId);
        values.put("senderId", dialog.senderId);
        values.put("receiverId", dialog.receiverId);
        values.put("content", dialog.content);
        values.put("time", dialog.time);
        values.put("isRead", dialog.isRead);
        db.update(DIALOG_TABLE, values, "messageId = ?", new String[]{dialog.messageId});
        db.close();
    }
    public void deleteDialog(Dialog dialog) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(DIALOG_TABLE, "messageId=?", new String[]{dialog.messageId});
        db.close();
    }
    public Dialog queryDialog(String messageId) {
        SQLiteDatabase db = getWritableDatabase();
        Dialog result = null;
        Cursor cursor = db.rawQuery("select * from " + DIALOG_TABLE + " where messageId=?", new String[]{messageId});
        if (cursor != null && cursor.moveToNext()) {
            result = new Dialog(
                    cursor.getString(cursor.getColumnIndex("messageId")),
                    cursor.getString(cursor.getColumnIndex("senderId")),
                    cursor.getString(cursor.getColumnIndex("receiverId")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    cursor.getString(cursor.getColumnIndex("time")),
                    cursor.getInt(cursor.getColumnIndex("isRead")));
            cursor.close();
        }
        db.close();
        return result;
    }
    public ArrayList<Dialog> getDialogList(Contact contact) {
        ArrayList<Dialog> result = new ArrayList<Dialog>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DIALOG_TABLE + " where senderId=? or receiverId=?", new String[]{contact.id, contact.id}, null);
        while (cursor.moveToNext()) {
            result.add(new Dialog(
                    cursor.getString(cursor.getColumnIndex("messageId")),
                    cursor.getString(cursor.getColumnIndex("senderId")),
                    cursor.getString(cursor.getColumnIndex("receiverId")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    cursor.getString(cursor.getColumnIndex("time")),
                    cursor.getInt(cursor.getColumnIndex("isRead"))));
        }
        return result;
    }
    public void setDialogRead(Contact contact) {
        ArrayList<Dialog> dialogList = getDialogList(contact);
        for (Dialog dialog:dialogList) {
            if (dialog.senderId == contact.id && dialog.isRead <= 0) {
                dialog.isRead = 1;
                updateDialog(dialog);
            }
        }
    }
    public void insertAddContactRequest(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", contact.id);
        values.put("name", contact.name);
        values.put("signature", contact.signature);
        db.insert(ADDCONTACT_TABLE, null, values);
        db.close();
    }
    public void deleteAddContactRequest(String contactId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ADDCONTACT_TABLE, "id=?", new String[]{contactId});
        db.close();
    }
    public ArrayList<Contact> getAddContactRequest() {
        ArrayList<Contact> result = new ArrayList<Contact>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ADDCONTACT_TABLE, null);
        while (cursor.moveToNext()) {
            result.add(new Contact(
                    cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("signature"))));
        }
        return result;
    }
}