package com.better_systematic_review.model;

import com.better_systematic_review.Main;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Review implements Serializable {
    private static Review current;
    private String name;
    private String id;
    private String lastLogin;
    private ArrayList<Document> documents;
    private File file;

    public Review(String name, String id, String lastLogin) {
        this.name = name;
        this.id = id;
        this.lastLogin = lastLogin;
        this.file = new File(Main.getReviewPath().resolve(id).toString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void addDocument(Document document) {
        documents.add(document);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
    }

    public ArrayList<Document> getDocuments(){
        return documents;
    }

    public File getFile() {
        return file;
    }

    public static void setCurrent(Review review) {
        current = review;
    }

    public static Review getCurrent(){
        return current;
    }

    public String toString() {
        return "Review: " + name + " ID: " + id;
    }
    public void save() {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
    public void delete() {
        file.delete();
    }
}
