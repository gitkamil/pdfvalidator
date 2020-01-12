package pdfvalidator;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.CertificateInfo;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import org.apache.pdfbox.pdmodel.PDDocument;

import org.apache.pdfbox.text.PDFTextStripper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;


public class TextValidation {

  //  @Test
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

   // @Test
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

  //  @Test
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

    @Test
    public void checkPdfCertificate() throws IOException, GeneralSecurityException {
        InputStream is = TextValidation.class.getClassLoader()
                .getResourceAsStream("jadlospisSigned.pdf");
        PdfReader reader = new PdfReader(is);
        boolean ok = verifySignature(reader);
       // LOGGER.info("'{}' is {}signed", "jadlospisSigned.pdf", ok ? "" : "NOT ");
    }


    public static final boolean verifySignature(PdfReader pdfReader)
            throws GeneralSecurityException, IOException {
        boolean valid = false;
        Security.addProvider(new BouncyCastleProvider());
        AcroFields acroFields = pdfReader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        acroFields.getFields().values().stream().forEach(System.out::println);
        System.out.println(signatureNames.size());
        System.out.println("signature: "+signatureNames.get(0));
        System.out.println(acroFields.signatureCoversWholeDocument(signatureNames.get(0)));
        PdfPKCS7 pk = acroFields.verifySignature(signatureNames.get(0));
        System.out.println("Subject: " + CertificateInfo.getSubjectFields(pk.getSigningCertificate()));
        System.out.println("Issuer: " + CertificateInfo.getIssuerFields(pk.getSigningCertificate()));
        System.out.println("Document verifies: " + pk.verify());
//        if (!signatureNames.isEmpty()) {
//            for (String name : signatureNames) {
//                if (acroFields.signatureCoversWholeDocument(name)) {
//                    PdfPKCS7 pkcs7 = acroFields.verifySignature(name);
//                    valid = pkcs7.verify();
//                    String reason = pkcs7.getReason();
//                    Calendar signedAt = pkcs7.getSignDate();
//                    X509Certificate signingCertificate = pkcs7.getSigningCertificate();
//                    Principal issuerDN = signingCertificate.getIssuerDN();
//                    Principal subjectDN = signingCertificate.getSubjectDN();
//                    System.out.println(issuerDN.toString());
////                    LOGGER.info("valid = {}, date = {}, reason = '{}', issuer = '{}', subject = '{}'",
////                            valid, signedAt.getTime(), reason, issuerDN, subjectDN);
//                    break;
//                }
//            }
//        }
        return valid;
    }
    static String getText(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        return new PDFTextStripper().getText(doc);
    }
}