package xyz.crabfish.nfccard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.sql.Date;
import java.text.SimpleDateFormat;

import xyz.crabfish.nfccard.R;
import xyz.crabfish.nfccard.db.MyDatabase;
import xyz.crabfish.nfccard.model.Note;

/**
 * Created by CrabFish on 2017/12/19.
 */

public class newNote extends AppCompatActivity {

    EditText ed1,ed2;
    ImageButton imageButton;
    MyDatabase myDatabase;
    Note note;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        ed1=(EditText) findViewById(R.id.editText1);
        ed2=(EditText) findViewById(R.id.editText2);
        imageButton=(ImageButton) findViewById(R.id.saveButton);
        myDatabase=new MyDatabase(this);

        Intent intent=this.getIntent();
        id=intent.getIntExtra("id", 0);
        //默认为0，不为0,则为修改数据时跳转过来的
        if(id!=0){
            note=myDatabase.getTiandCon(id);
            ed1.setText(note.getTitle());
            ed2.setText(note.getContent());
        }
        //保存按钮的点击事件，他和返回按钮是一样的功能，所以都调用isSave()方法；
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isSave();
            }
        });
    }

    /*
     * 返回按钮调用的方法。
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String times = formatter.format(curDate);
        String title=ed1.getText().toString();
        String content=ed2.getText().toString();
        //是要修改数据
        if(id!=0){
            note=new Note(title,id, content, times);
            myDatabase.toUpdate(note);
            Intent intent=new Intent(newNote.this,MainActivity.class);
            startActivity(intent);
            newNote.this.finish();
        }
        //新建日记
        else{
            if(title.equals("")&&content.equals("")){
                Intent intent=new Intent(newNote.this,MainActivity.class);
                startActivity(intent);
                newNote.this.finish();
            }
            else{
                note=new Note(title,content,times);
                myDatabase.toInsert(note);
                Intent intent=new Intent(newNote.this,MainActivity.class);
                startActivity(intent);
                newNote.this.finish();
            }

        }
    }
    private void isSave(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String times = formatter.format(curDate);
        String title=ed1.getText().toString();
        String content=ed2.getText().toString();
        //是要修改数据
        if(id!=0){
            note=new Note(title,id, content, times);
            myDatabase.toUpdate(note);
            Intent intent=new Intent(newNote.this,MainActivity.class);
            startActivity(intent);
            newNote.this.finish();
        }
        //新建日记
        else{
            note=new Note(title,content,times);
            myDatabase.toInsert(note);
            Intent intent=new Intent(newNote.this,MainActivity.class);
            startActivity(intent);
            newNote.this.finish();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.second, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Intent intent=new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT,
//                        "标题："+ed1.getText().toString()+"    " +
//                                "内容："+ed2.getText().toString());
//                startActivity(intent);
//                break;
//
//            default:
//                break;
//        }
//        return false;
//    }
}
