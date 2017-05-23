package com.excellence.user.blockx.models;

import com.orm.SugarRecord;

/**
 * Created by User on 1/19/2017.
 */

public class Post extends SugarRecord {


    private long post_id;
    private int user_id;
    private String memberImageUrl;
    private String postedImageUrl;
    private String thumbNail;
    private String title;
    private String date;
    private String message;
    private String posterFullName;
    private String posterPosition;
    private String timeStamp;
    private String url;
    private String postedVideoUrl;
    private String postedImageFileName;
    private int num_comments;
    private int num_likes;



    public Post(){}

    public Post(long id, String message, String memberImageUrl, String displayName, String posterPosition,
                String postedImageUrl, String postedVideoUrl, String thumbNail){
        setId(id);
        this.message = message;
        this.memberImageUrl = memberImageUrl;
        this.posterFullName = displayName;
        this.posterPosition = posterPosition;
        this.postedImageUrl = postedImageUrl;
        this.postedVideoUrl = postedVideoUrl;
        this.thumbNail = thumbNail;
    }




    public String getPostedVideoUrl() {
        return postedVideoUrl;
    }

    public Post setPostedVideoUrl(String postedVideoUrl) {
        this.postedVideoUrl = postedVideoUrl;
        return this;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public Post setThumbNail(String thumbNail) {
        this.thumbNail = thumbNail;
        return this;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Post setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Post setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getPostedImageUrl() {
        return postedImageUrl;
    }

    public Post setPostedImageUrl(String postedImageUrl) {
        this.postedImageUrl = postedImageUrl;
        return this;
    }

    public String getPosterPosition() {
        return posterPosition;
    }

    public Post setPosterPosition(String posterPosition) {
        this.posterPosition = posterPosition;
        return this;
    }

    public String getPosterFullName() {
        return posterFullName;
    }

    public Post setPosterFullName(String posterFullName) {
        this.posterFullName = posterFullName;
        return this;
    }

    public int getUser_id() {
        return user_id;
    }

    public Post setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Post setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Post setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Post setMessage(String message) {
        this.message = message;
        return this;
    }

    public int getNum_comments() {
        return num_comments;
    }

    public Post setNum_comments(int num_comments) {
        this.num_comments = num_comments;
        return this;
    }

    public int getNum_likes() {
        return num_likes;
    }

    public Post setNum_likes(int num_likes) {
        this.num_likes = num_likes;
        return this;
    }

    public String getMemberImageUrl() {
        return memberImageUrl;
    }

    public Post setMemberImageUrl(String memberImageUrl) {
        this.memberImageUrl = memberImageUrl;
        return this;
    }

    public static Post getPost(int post_id){
        //TODO:get post
        return new Post();
    }

    @Override
    public Long getId() {
        return post_id;
    }

    @Override
    public void setId(Long id) {
        this.post_id = id;
        super.setId(id);
    }
}



