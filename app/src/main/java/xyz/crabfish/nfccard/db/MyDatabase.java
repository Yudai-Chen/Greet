package xyz.crabfish.nfccard.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import xyz.crabfish.nfccard.model.Note;

/**
 * Created by CrabFish on 2017/12/19.
 */

public class MyDatabase {

    Context context;
    MyDatabaseHelper myHelper;
    SQLiteDatabase myDatabase;
    /*
     * 别的类实例化这个类的同时，创建数据库
     */
    public MyDatabase(Context context){
        this.context=context;
        myHelper = new MyDatabaseHelper(context);
    }
    /*
     * 得到ListView的数据，从数据库里查找后解析
     */
    public ArrayList<Note> getArray(){
        ArrayList<Note> array = new ArrayList<Note>();
        ArrayList<Note> array1 = new ArrayList<Note>();
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select id,title,time from notes" , null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String times = cursor.getString(cursor.getColumnIndex("time"));
            Note note = new Note(id, title, times);
            array.add(note);
            cursor.moveToNext();
        }
        myDatabase.close();
        for (int i = array.size(); i >0; i--) {
            array1.add(array.get(i-1));
        }
        return array1;
    }

    /*
     * 返回可能要修改的数据
     */
    public Note getTiandCon(int id){
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor=myDatabase.rawQuery("select title,content from notes where id='"+id+"'" , null);
        cursor.moveToFirst();
        String title=cursor.getString(cursor.getColumnIndex("title"));
        String content=cursor.getString(cursor.getColumnIndex("content"));
        Note note=new Note(title,content);
        myDatabase.close();
        return note;
    }

    /*
     * 用来修改日记
     */
    public void toUpdate(Note note){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL(
                "update notes set title='"+ note.getTitle()+
                        "',time='"+note.getTime()+
                        "',content='"+note.getContent() +
                        "' where id='"+ note.getId()+"'");
        myDatabase.close();
    }

    /*
     * 用来增加新的日记
     */
    public void toInsert(Note note){
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("insert into notes(title,content,time)values('"
                + note.getTitle()+"','"
                +note.getContent()+"','"
                +note.getTime()
                +"')");
        myDatabase.close();
    }

    /*
     * 长按点击后选择删除日记
     */
    public void toDelete(int id){
        myDatabase  = myHelper.getWritableDatabase();
        myDatabase.execSQL("delete from notes where id="+id+"");
        myDatabase.close();
    }
}
