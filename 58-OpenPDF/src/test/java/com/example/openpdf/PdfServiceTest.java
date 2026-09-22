package com.example.openpdf;

import com.example.openpdf.service.PdfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfServiceTest {

    @Autowired
    private PdfService pdfService;

    @Test
    void testCreateInvoicePdfBytes() {
        byte[] pdf = pdfService.generateInvoicePdf(101L);
        assertNotNull(pdf, "PDF should not be null");
        assertTrue(pdf.length > 0, "PDF should have content");
    }

    @Test
    void testReportPdfBytes() {
        byte[] pdf = pdfService.generateReportPdf();
        assertNotNull(pdf, "PDF should not be null");
        assertTrue(pdf.length > 0, "PDF should have content");
    }

    @Test
    void testPdfStartsWithMagicNumber() {
        byte[] pdf = pdfService.generateInvoicePdf(101L);
        // PDF files start with %PDF-
        assertEquals((byte) '%', pdf[0]);
        assertEquals((byte) 'P', pdf[1]);
        assertEquals((byte) 'D', pdf[2]);
        assertEquals((byte) 'F', pdf[3]);
        assertEquals((byte) '-', pdf[4]);
    }
    
    @Test
    void testReportPdfStartsWithMagicNumber() {
        byte[] pdf = pdfService.generateReportPdf();
        // PDF files start with %PDF-
        assertEquals((byte) '%', pdf[0]);
        assertEquals((byte) 'P', pdf[1]);
        assertEquals((byte) 'D', pdf[2]);
        assertEquals((byte) 'F', pdf[3]);
        assertEquals((byte) '-', pdf[4]);
    }
}
