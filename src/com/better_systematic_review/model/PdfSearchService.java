package com.better_systematic_review.model;

import com.better_systematic_review.controller.ReviewScreen;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class searches through the text of multiple PDFs and maps each one to
 * the number of matches found for the search text. It supports ignoring case
 * and searching with regular expressions.
 */
public class PdfSearchService extends Service<Map<ReviewScreen.TableDocument, Integer>> {

    private List<ReviewScreen.TableDocument> docs;
    private String searchText;
    private Pattern pattern;
    private boolean ignoreCase;
    private boolean regexMode;

    public static final int FILE_ERROR_FLAG = -1;

    public void setDocs(List<ReviewScreen.TableDocument> docs) {
        this.docs = docs;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public void setRegexMode(boolean regexMode) {
        this.regexMode = regexMode;
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    protected Task<Map<ReviewScreen.TableDocument, Integer>> createTask() {
        return new Task<Map<ReviewScreen.TableDocument, Integer>>() {
            @Override
            public Map<ReviewScreen.TableDocument, Integer> call() throws Exception {
                updateProgress(0, 1);
                compilePattern();
                HashMap<ReviewScreen.TableDocument, Integer> results = new HashMap<>();

                int docNumber = 1;
                for (ReviewScreen.TableDocument tableDoc : docs) {
                    results.put(tableDoc, countMatches(tableDoc));
                    updateProgress(docNumber++, docs.size()); // For progress bar
                }

                return results;
            }
        };
    }

    /**
     * Counts the number of matches for the current search text in the text of
     * the given document.
     *
     * @param doc The document
     *
     * @return The number of matches for the search text in the document
     */
    private int countMatches(ReviewScreen.TableDocument doc) {
        int result = 0;
        String text;

        try {
            text = documentAsString(doc.getDocument());
        } catch (IOException e) {
            return FILE_ERROR_FLAG;
        }

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            result++;
        }

        return result;
    }

    /**
     * Compiles the current search text into a Pattern that can be reused for
     * each document.
     */
    private void compilePattern() {
        int flags = 0;

        if (ignoreCase) {
            flags |= Pattern.CASE_INSENSITIVE;
        }

        if (!regexMode) {
            flags |= Pattern.LITERAL;
        }

        pattern = Pattern.compile(searchText, flags);
    }

    /**
     * Goes to the .txt file containing the text of the given document and
     * returns the contents of the file as a string.
     *
     * @param doc The document
     *
     * @return The text of the document as a string
     *
     * @throws IOException If the file fails to read
     */
    private String documentAsString(Document doc) throws IOException {
        String pathToPDF = doc.getFile().getAbsolutePath();
        String pathToTXT = pathToPDF.replace(".pdf",  ".txt");
        Path path = (new File(pathToTXT)).toPath();
        byte[] data = Files.readAllBytes(path);
        return new String(data);
    }
}
