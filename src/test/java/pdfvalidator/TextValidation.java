package pdfvalidator;

import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class TextValidation {

    @Test
    public void getPdfAsText() {
        System.out.println();

        String filename = "C:\\Users\\Kamil\\repo\\pdfvalidator\\src\\test\\resources\\pdfToValidate.pdf";

        try {
            String text = getText(new File(filename));
            System.out.println("Text in PDF: " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSecondPageAsText() throws IOException {
        System.out.println();


        String filename = "C:\\Users\\Kamil\\repo\\pdfvalidator\\src\\test\\resources\\pdfToValidate.pdf";
        PDDocument doc = PDDocument.load(new File(filename));
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(2);
        stripper.setEndPage(3);

        try {
            String text = stripper.getText(doc);
            System.out.println("Text in PDF: " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSecondPageAsTextAndCheckName() throws IOException {

        String filename = getClass().getClassLoader().getResource("pdfToValidate.pdf").getPath();
        String name = "Marek";
        String[] lines = null;

        PDDocument doc = PDDocument.load(new File(filename));
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(2);
        stripper.setEndPage(2);

        try {
            String text = stripper.getText(doc);
            lines = text.split("\\r?\\n");
            Stream<String> stream = Stream.of(lines);
            boolean result = stream.anyMatch(str -> str.equals("Imie: " + name + " "));
            System.out.println("result: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static String getText(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        return new PDFTextStripper().getText(doc);
    }
}