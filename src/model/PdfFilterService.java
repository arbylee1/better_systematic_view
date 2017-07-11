package model;

import controller.ReviewScreen;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import java.util.ArrayList;
import java.util.List;
import org.jpedal.examples.text.FindTextInRectangle;
import org.jpedal.exception.PdfException;
import org.jpedal.grouping.SearchType;

public class PdfFilterService extends Service<List<ReviewScreen.TableDocument>> {

    private List<ReviewScreen.TableDocument> docs;
    private String searchText;

    public void setDocs(List<ReviewScreen.TableDocument> docs) {
        this.docs = docs;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    protected Task<List<ReviewScreen.TableDocument>> createTask() {
        return new Task<List<ReviewScreen.TableDocument>>() {
            @Override
            public List<ReviewScreen.TableDocument> call() throws Exception {
                List<ReviewScreen.TableDocument> docsWithNoResults = new ArrayList<>();
                updateProgress(0, 1);
                int docIndex = 1;

                for (ReviewScreen.TableDocument doc : docs) {
                    String path = doc.getFile().getAbsolutePath();
                    FindTextInRectangle extract = new FindTextInRectangle(path);
                    boolean hit = false;

                    try {
                        if (extract.openPDFFile()) {
                            for (int page = 1; page <= extract.getPageCount() && !hit; page++) {
                                float[] resultsThisPage = extract.findTextOnPage(page, searchText, SearchType.MUTLI_LINE_RESULTS);

                                if (resultsThisPage.length > 0) {
                                    hit = true;
                                }
                            }
                        }
                    } catch (PdfException e) {
                        e.printStackTrace();
                    }

                    if (!hit) {
                        docsWithNoResults.add(doc);
                    }

                    updateProgress(docIndex++, docs.size());
                }

                return docsWithNoResults;
            }
        };
    }
}
