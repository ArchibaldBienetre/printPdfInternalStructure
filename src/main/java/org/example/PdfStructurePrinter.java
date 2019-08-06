package org.example;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;

import java.io.PrintStream;

class PdfStructurePrinter {

    void print(String filePath, PrintStream out) throws Exception {
        PdfReader reader = new PdfReader(filePath);

        out.println("### ROOT ###");
        PdfDictionary root = reader.getCatalog();
        printRecursively(reader, root, out, "");

        out.println();
        out.println("### Pages ###");
        int pageCount = reader.getNumberOfPages();
        for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
            out.println();
            out.println("## Page " + pageNumber + " ##");
            PdfDictionary page = reader.getPageN(pageNumber);
            printRecursively(reader, page, out, "");
        }
    }

    private void printRecursively(PdfReader reader, PdfDictionary dictionary, PrintStream out, String prefix) {
        for (Object keyObject : dictionary.getKeys()) {
            PdfName key = (PdfName) keyObject;
            PdfObject value = dictionary.get(key);
            String outputFirstPart = prefix + key.toString() + ": (" + value.getClass().getSimpleName() + "] ";
            if (value.isDictionary()) {
                out.println(outputFirstPart);
                printRecursively(reader, dictionary.getAsDict(key), out, prefix + "\t");
            } else {
                out.println(outputFirstPart + value.toString());
            }
        }
    }
}
