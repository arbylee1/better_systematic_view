package better_systematic_view;

public class Document {

    private String[] authors;
    private String title;
    private String year;
    private String authorsString;
    private int id;

    Document(String[] authors, String title, String year, int id) {
        this.authors = authors;
        this.title = title;
        this.year = year;
        this.authorsString = String.join(",", authors);
        this.id = id;
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

    public int getId() {
        return id;
    }
}
