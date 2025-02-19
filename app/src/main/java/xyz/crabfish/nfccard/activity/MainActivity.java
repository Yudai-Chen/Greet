package xyz.crabfish.nfccard.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mzule.fantasyslide.SideBar;
import com.github.mzule.fantasyslide.SimpleFantasyListener;
import com.github.mzule.fantasyslide.Transformer;

import java.util.ArrayList;
import java.util.List;

import xyz.crabfish.nfccard.R;
import xyz.crabfish.nfccard.db.MyAdapter;
import xyz.crabfish.nfccard.db.MyDatabase;
import xyz.crabfish.nfccard.model.Note;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    ImageButton imageButton;
    ListView listView;
    MyDatabase mDb;
    LayoutInflater inflater;
    ArrayList<Note> array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final DrawerArrowDrawable indicator = new DrawerArrowDrawable(this);
        indicator.setColor(Color.WHITE);
        getSupportActionBar().setHomeAsUpIndicator(indicator);

        setTransformer();
        // setListener();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (((ViewGroup) drawerView).getChildAt(1).getId() == R.id.leftSideBar) {
                 //   indicator.setProgress(slideOffset);
                }
            }
        });



        listView = (ListView) findViewById(R.id.note_list);
        mDb=new MyDatabase(this);
        array=mDb.getArray();
        inflater=getLayoutInflater();
        MyAdapter adapter=new MyAdapter(inflater,array);
        listView.setAdapter(adapter);
        imageButton = (ImageButton)findViewById(R.id.btnAdd);
        listView.setAdapter(adapter);
      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent=new Intent(getApplicationContext(),newNote.class);
                intent.putExtra("id",array.get(position).getId() );
                startActivity(intent);
                MainActivity.this.finish();
            }


        });*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent=new Intent(getApplicationContext(),newNote.class);
                intent.putExtra("id",array.get(position).getId() );
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        /*
         * 长点后来判断是否删除数据
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                //AlertDialog,来判断是否删除日记。
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("删除")
                        .setMessage("是否删除笔记")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDb.toDelete(array.get(position).getId());
                                array=mDb.getArray();
                                MyAdapter adapter=new MyAdapter(inflater,array);
                                listView.setAdapter(adapter);
                            }
                        })
                        .create().show();
                return true;
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),newNote.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
    }

    private void setListener() {
        final TextView tipView = (TextView) findViewById(R.id.tipView);
        SideBar leftSideBar = (SideBar) findViewById(R.id.leftSideBar);
        leftSideBar.setFantasyListener(new SimpleFantasyListener() {
            @Override
            public boolean onHover(@Nullable View view, int index) {
                tipView.setVisibility(View.VISIBLE);
                if (view instanceof TextView) {
                    tipView.setText(String.format("%s at %d", ((TextView) view).getText().toString(), index));
                } else if (view != null && view.getId() == R.id.userInfo) {
                    tipView.setText(String.format("个人中心 at %d", index));
                } else {
                    tipView.setText(null);
                }
                return false;

            }

            @Override
            public boolean onSelect(View view, int index) {
                tipView.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, String.format("%d selected", index), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onCancel() {
                tipView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setTransformer() {
        final float spacing = getResources().getDimensionPixelSize(R.dimen.spacing);
        //SideBar rightSideBar = (SideBar) findViewById(R.id.rightSideBar);
//        rightSideBar.setTransformer(new Transformer() {
//            private View lastHoverView;
//
//            @Override
//            public void apply(ViewGroup sideBar, View itemView, float touchY, float slideOffset, boolean isLeft) {
//                boolean hovered = itemView.isPressed();
//                if (hovered && lastHoverView != itemView) {
//                    animateIn(itemView);
//                    animateOut(lastHoverView);
//                    lastHoverView = itemView;
//                }
//            }
//
//            private void animateOut(View view) {
//                if (view == null) {
//                    return;
//                }
//                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", -spacing, 0);
//                translationX.setDuration(200);
//                translationX.start();
//            }
//
//            private void animateIn(View view) {
//                ObjectAnimator translationX = ObjectAnimator.ofFloat(view, "translationX", 0, -spacing);
//                translationX.setDuration(200);
//                translationX.start();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        return true;
    }

    public void onClick(View view) {
        if (view instanceof TextView) {
            String title = ((TextView) view).getText().toString();
            if (title.startsWith("星期")) {
                Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
            }
            else if(title.equals("文件传输")){
                startActivity(FileTransport.newIntent(this, title));
            }
            else {
                startActivity(UniversalActivity.newIntent(this, title));
            }
        } else if (view.getId() == R.id.userInfo) {
            startActivity(editData.newIntent(this, "个人中心"));
        }
    }
}