package com.better_systematic_review.model;

import com.better_systematic_review.Main;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Review implements Serializable {
    private String name;
    private String id;
    private String lastLogin;
    private Set<Document> documents;
    private File file;

    public Review(String name, String id, String lastLogin) {
        this.name = name;
        this.id = id;
        this.lastLogin = lastLogin;
        this.file = new File(Main.getReviewPath().resolve(id).toString());
        this.documents = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        save();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        save();
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
        save();
    }

    public void addDocument(Document document) {
        documents.add(document);
        save();
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        save();
    }

    public Set<Document> getDocuments(){
        return documents;
    }

    public File getFile() {
        return file;
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
