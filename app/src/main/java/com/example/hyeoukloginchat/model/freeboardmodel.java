package com.example.hyeoukloginchat.model;

public class freeboardmodel {
    public  String uid;
    public String title;
    public String contents;
    public  String name;
    public String imageurl;
    public String time;
    public String id;

    public freeboardmodel(String title, String contents,String name,String uid,String imageurl,String time,String id) {
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.uid =uid;
        this.imageurl = imageurl;
        this.time =time;
        this.id = id;

    }
    public freeboardmodel(String title, String name,String time,String imageurl,String id,String uid) {
        this.title = title;
        this.name = name;
        this.time = time;
        this.imageurl = imageurl;
        this.id = id;
        this.uid = uid;


    }

    public  String getId(){return  this.id;}
    public  void setId(String id){this.id=id;}
    public  String getTime(){return  this.time;}
    public  void setTime(String time){this.time=time;}
    public  String getUid(){return  this.uid;}
    public  void setUid(String uid){this.uid=uid;}
    public  String getTitle(){return  this.title;}
    public  void setTitle(String title){this.title=title;}
    public  String getContents(){return  this.contents;}
    public  void setContents(String contents){ this.contents=contents;}
    public  String getName(){return  this.name;}
    public  void setName(String name){ this.name=name;}
    public  String getImageurl(){return  this.imageurl;}
    public  void setImageurl(String imageurl){this.imageurl=imageurl;}
}
