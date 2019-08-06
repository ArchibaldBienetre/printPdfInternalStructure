package org.example;

import com.lowagie.text.pdf.*;

import java.io.PrintStream;

class PdfStructurePrinter {

    void print(String filePath, PrintStream out) throws Exception {
        PdfReader reader = new PdfReader(filePath);

        out.println("### ROOT ###");
        PdfDictionary root = reader.getCatalog();
        printRecursively(root, out, "");

        out.println();
        out.println("### Pages ###");
        int pageCount = reader.getNumberOfPages();
        for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
            out.println();
            out.println("## Page " + pageNumber + " ##");
            PdfDictionary page = reader.getPageN(pageNumber);
            printRecursively(page, out, "");
        }
    }

    private void printRecursively(PdfDictionary dictionary, PrintStream out, String prefix) {
        for (Object keyObject : dictionary.getKeys()) {
            PdfName key = (PdfName) keyObject;
            PdfObject value = dictionary.get(key);
            String outputFirstPart = prefix + key.toString() + ": (" + value.getClass().getSimpleName() + "] ";
            if (value.isIndirect()) {
                PdfIndirectReference indirect = dictionary.getAsIndirectObject(key);
                value = PdfReader.getPdfObject(indirect);
            }
            if (value.isDictionary()) {
                out.println(outputFirstPart);
                printRecursively(dictionary.getAsDict(key), out, prefix + "\t");
            } else {
                out.println(outputFirstPart + value.toString());
            }
        }
    }
}
