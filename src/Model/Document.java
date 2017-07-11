package Model;

import java.io.File;
import java.util.Arrays;

public class Document {

    private String[] authors;
    private String title;
    private String year;
    private String authorsString;
    private File file;

    public Document(String[] authors, String title, String year, File file) {
        this.authors = authors;
        this.title = title;
        this.year = year;
        this.authorsString = String.join(",", authors);
        this.file = file;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getAuthorsString() {
        return authorsString;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Document)) {
            return false;
        }

        Document that = (Document) other;

        if (!Arrays.equals(this.authors, that.authors)) {
            return false;
        }

        if (!this.title.equals(that.title)) {
            return false;
        }

        if (!this.year.equals(that.year)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 17;

        hash = 31 * hash + Arrays.hashCode(authors);
        hash = 31 * hash + title.hashCode();
        hash = 31 * hash + year.hashCode();
        hash = 31 * hash + authorsString.hashCode();

        return hash;
    }
}
