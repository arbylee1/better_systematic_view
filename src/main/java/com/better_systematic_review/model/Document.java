package com.better_systematic_review.model;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

public class Document implements Serializable{

    private int hash;
    private File file;
    private String title;
    private String year;
    private String[] authors;
    private String authorsString;

    public Document(File file, String title, String year, String[] authors) {
        this.file = file;
        this.title = title;
        this.year = year;
        this.authors = authors;
        this.authorsString = String.join(", ", authors);
    }

    File getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getAuthorsString() {
        return authorsString;
    }

    public boolean equivalent(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Document)) {
            return false;
        }

        if (this == other) {
            return true;
        }

        Document that = (Document) other;

        return this.file.equals(that.file)
                && this.title.equals(that.title)
                && this.year.equals(that.year)
                && Arrays.equals(this.authors, that.authors);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Document)) {
            return false;
        }

        if (this == other) {
            return true;
        }

        Document that = (Document) other;

        return this.file.equals(that.file)
            && this.title.equals(that.title)
            && this.year.equals(that.year)
            && Arrays.equals(this.authors, that.authors);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = 17;
            hash = 31 * hash + file.hashCode();
            hash = 31 * hash + title.hashCode();
            hash = 31 * hash + year.hashCode();
            hash = 31 * hash + Arrays.hashCode(authors);
        }

        return hash;
    }
}
