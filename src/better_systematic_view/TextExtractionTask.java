package better_systematic_view;

import javafx.concurrent.Task;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextExtractionTask extends Task<Void> {

    private File pdfFile;

    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    protected Void call() {
        String docPath = pdfFile.getAbsolutePath();
        String textPath = docPath.replace(".pdf", ".txt");
        File textFile = new File(textPath);

        try {
            PDDocument pdf = PDDocument.load(pdfFile);
            FileWriter textWriter = new FileWriter(textFile);
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.writeText(pdf, textWriter);
            textWriter.close();
            pdf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
