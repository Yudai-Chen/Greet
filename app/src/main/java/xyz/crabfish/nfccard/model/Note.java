package xyz.crabfish.nfccard.model;

/**
 * Created by CrabFish on 2017/12/19.
 */

public class Note {
    private String title;   //标题
    private String content; //内容
    private String time;   //时间
    private int id;        //编号

    public Note(String title, int id, String content , String time){
        this.id=id;
        this.title=title;
        this.content=content;
        this.time=time;
    }

    public Note(String title, String content, String time){
        this.title=title;
        this.content=content;
        this.time=time;
    }

    public Note(int id, String title, String time){
        this.id=id;
        this.title=title;
        this.time=time;
    }

    public Note(String title, String content){
        this.title=title;
        this.content=content;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setContent(String content){
        this.content = content;
    }

    public void setTime(String time){
        this.time = time;
    }
    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}
