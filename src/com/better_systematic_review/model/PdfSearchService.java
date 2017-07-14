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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfSearchService extends Service<HashMap<ReviewScreen.TableDocument, Integer>> {

    private List<ReviewScreen.TableDocument> docs;
    private String searchText;
    private Pattern pattern;
    private boolean ignoreCase;
    private boolean regexMode;

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
    protected Task<HashMap<ReviewScreen.TableDocument, Integer>> createTask() {
        return new Task<HashMap<ReviewScreen.TableDocument, Integer>>() {
            @Override
            public HashMap<ReviewScreen.TableDocument, Integer> call() throws Exception {
                HashMap<ReviewScreen.TableDocument, Integer> results = new HashMap<>();
                updateProgress(0, 1);
                compilePattern();

                int docIndex = 0;
                for (ReviewScreen.TableDocument tableDoc : docs) {
                    results.put(tableDoc, 0);

                    try {
                        String text = documentAsString(tableDoc.getDocument());
                        Matcher matcher = pattern.matcher(text);

                        while (matcher.find()) {
                            results.put(tableDoc, results.get(tableDoc) + 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    updateProgress(++docIndex, docs.size());
                }

                return results;
            }
        };
    }

    private void compilePattern() {
        int flags = 0;

        if (ignoreCase) {
            flags |= Pattern.CASE_INSENSITIVE;
        }

        if (!regexMode) {
            flags |= Pattern.LITERAL;
        }

        if (flags == 0) {
            pattern = Pattern.compile(searchText);
        } else {
            pattern = Pattern.compile(searchText, flags);
        }
    }

    private String documentAsString(Document doc) throws IOException {
        String pathToPDF = doc.getFile().getAbsolutePath();
        String pathToTXT = pathToPDF.replace(".pdf",  ".txt");
        Path path = (new File(pathToTXT)).toPath();
        byte[] data = Files.readAllBytes(path);
        return new String(data);
    }
}
