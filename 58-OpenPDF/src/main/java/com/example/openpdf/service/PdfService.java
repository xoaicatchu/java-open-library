package com.example.openpdf.service;

import com.example.openpdf.dto.InvoiceDto;
import com.example.openpdf.dto.ProductDto;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(Long orderId) {
        // Mock data
        InvoiceDto invoice = new InvoiceDto(
                orderId,
                "Nguyen Van A",
                List.of(
                        new ProductDto("Laptop", 1, new BigDecimal("1500.00"), new BigDecimal("1500.00")),
                        new ProductDto("Mouse", 2, new BigDecimal("25.00"), new BigDecimal("50.00"))
                ),
                new BigDecimal("1550.00")
        );
        return createInvoice(invoice);
    }

    public byte[] generateReportPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            
            HeaderFooter header = new HeaderFooter(new Phrase("Company Report"), false);
            header.setAlignment(Element.ALIGN_CENTER);
            document.setHeader(header);

            HeaderFooter footer = new HeaderFooter(new Phrase("Page: "), true);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.setFooter(footer);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph title = new Paragraph("Product Sales Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            PdfPCell h1 = new PdfPCell(new Phrase("Product Name", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            h1.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Sales", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            h2.setBackgroundColor(Color.LIGHT_GRAY);
            table.addCell(h2);

            table.addCell("Laptop");
            table.addCell("120");
            table.addCell("Mouse");
            table.addCell("350");

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating report", e);
        }
    }

    private byte[] createInvoice(InvoiceDto invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Font styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

            // Title
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Invoice Details
            document.add(new Paragraph("Order ID: " + invoice.orderId(), normalFont));
            document.add(new Paragraph("Customer: " + invoice.customerName(), normalFont));
            document.add(new Paragraph("Date: " + LocalDate.now(), normalFont));
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Headers
            String[] headers = {"Product", "Quantity", "Price", "Subtotal"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(Color.ORANGE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Data
            for (ProductDto p : invoice.products()) {
                table.addCell(new PdfPCell(new Phrase(p.name(), normalFont)));
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(p.quantity()), normalFont));
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qtyCell);
                table.addCell(new PdfPCell(new Phrase(p.price().toString(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(p.subtotal().toString(), normalFont)));
            }
            document.add(table);

            // Total Amount
            Paragraph total = new Paragraph("Total: $" + invoice.totalAmount(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.RED));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            
            // Image (Generated dynamically)
            document.add(Chunk.NEWLINE);
            Image img = Image.getInstance(generateLogo(), null);
            img.setAlignment(Element.ALIGN_CENTER);
            document.add(img);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private java.awt.Image generateLogo() {
        BufferedImage img = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 100, 50);
        g2d.setColor(Color.BLUE);
        g2d.drawString("MY LOGO", 20, 30);
        g2d.dispose();
        return img;
    }
}
