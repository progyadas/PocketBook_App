package com.project.pocketbookapp.Model;

public class ModelBookSuggestions {
    String id,timestamp,bookName,uid;

    public ModelBookSuggestions() {
    }

    public ModelBookSuggestions(String id, String timestamp, String bookName, String uid) {
        this.id = id;
        this.timestamp = timestamp;
        this.bookName = bookName;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
