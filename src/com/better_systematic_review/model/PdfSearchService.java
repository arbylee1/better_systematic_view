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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                HashMap<ReviewScreen.TableDocument, Integer> results = new HashMap<>();
                updateProgress(0, 1);
                compilePattern();

                int docIndex = 0;
                for (ReviewScreen.TableDocument tableDoc : docs) {
                    results.put(tableDoc, countMatches(tableDoc));
                    updateProgress(++docIndex, docs.size());
                }

                return results;
            }
        };
    }

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

    private void compilePattern() {
        int flags = 0;

        if (ignoreCase) {
            flags |= Pattern.CASE_INSENSITIVE;
        }

        if (!regexMode) {
            flags |= Pattern.LITERAL;
        }

        pattern = (flags == 0)
            ? Pattern.compile(searchText)
            : Pattern.compile(searchText, flags);
    }

    private String documentAsString(Document doc) throws IOException {
        String pathToPDF = doc.getFile().getAbsolutePath();
        String pathToTXT = pathToPDF.replace(".pdf",  ".txt");
        Path path = (new File(pathToTXT)).toPath();
        byte[] data = Files.readAllBytes(path);
        return new String(data);
    }
}
