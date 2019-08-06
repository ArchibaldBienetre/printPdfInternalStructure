package org.example;

import com.lowagie.text.pdf.*;

import java.io.PrintStream;

class PdfStructurePrinter {

    // either this, or track used-up indirect references to not end up with potential stack overflows
    private static final int MAX_RECURSION_DEPTH = 20;
    private static final String SPACER = "  ";

    void print(String filePath, PrintStream out) throws Exception {
        PdfReader reader = new PdfReader(filePath);

//        printFromRoot(out, reader);

        // alternatively, print pages:
        printPages(out, reader);
    }

    private void printFromRoot(PrintStream out, PdfReader reader) throws Exception {
        out.println("### ROOT ###");
        PdfDictionary root = reader.getCatalog();
        printRecursively(root, out, "");
    }

    private void printPages(PrintStream out, PdfReader reader) throws Exception {
        out.println("### Pages ###");
        int pageCount = reader.getNumberOfPages();
        for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
            out.println();
            out.println("## Page " + pageNumber + " ##");
            PdfDictionary page = reader.getPageN(pageNumber);
            printRecursively(page, out, "");
        }
    }

    private void printRecursively(PdfDictionary dictionary, PrintStream out, String prefix) throws Exception {
        for (Object keyObject : dictionary.getKeys()) {
            PdfName key = (PdfName) keyObject;
            PdfObject value = dictionary.get(key);
            printDictionaryValue(dictionary, out, prefix, key, value);
        }
    }

    private void printRecursively(PdfArray array, PrintStream out, String prefix) throws Exception {
        int arraySize = array.size();
        for (int arrayIndex = 0; arrayIndex < arraySize; arrayIndex++) {
            printArrayValue(array, out, prefix + SPACER, arrayIndex);
        }
    }

    private void printDictionaryValue(PdfDictionary dictionary, PrintStream out, String prefix, PdfName key, PdfObject value) throws Exception {
        if (maxRecursionDepthReached(prefix)) {
            out.println(prefix + "[BREAK] Maximum recursion depth reached!");
            return;
        }
        String outputFirstPart = prefix + key.toString() + ": (" + value.getClass().getSimpleName() + "] ";
        value = resolveIfIndirect(dictionary, key, value);
        if (value.isDictionary()) {
            out.println(outputFirstPart);
            printRecursively(dictionary.getAsDict(key), out, prefix + SPACER);
        } else if (value.isArray()) {
            out.println(outputFirstPart);
            PdfArray array = dictionary.getAsArray(key);
            printRecursively(array, out, prefix);
        } else if (value.isStream()) {
            out.println(outputFirstPart);
            PdfStream stream = dictionary.getAsStream(key);
            printStream(out, prefix, stream);
        } else {
            out.println(outputFirstPart + value.toString());
        }
    }

    private boolean maxRecursionDepthReached(String prefix) {
        return prefix.length() > MAX_RECURSION_DEPTH * SPACER.length();
    }

    private PdfObject resolveIfIndirect(PdfDictionary dictionary, PdfName key, PdfObject value) {
        if (value.isIndirect()) {
            PdfIndirectReference indirect = dictionary.getAsIndirectObject(key);
            value = PdfReader.getPdfObject(indirect);
        }
        return value;
    }

    private void printArrayValue(PdfArray array, PrintStream out, String prefix, int arrayIndex) throws Exception {
        if (maxRecursionDepthReached(prefix)) {
            out.println("[BREAK] Maximum recursion depth reached!");
            return;
        }
        PdfObject value = array.getPdfObject(arrayIndex);
        value = resolveIfIndirect(array, arrayIndex, value);
        String outputFirstPart = prefix + "(" + arrayIndex + "): (" + value.getClass().getSimpleName() + "] ";
        if (value.isDictionary()) {
            out.println(outputFirstPart);
            printRecursively(array.getAsDict(arrayIndex), out, prefix + SPACER);
        } else if (value.isArray()) {
            out.println(outputFirstPart);
            PdfArray nestedArray = array.getAsArray(arrayIndex);
            printRecursively(nestedArray, out, prefix);
        } else if (value.isStream()) {
            out.println(outputFirstPart);
            PdfStream stream = array.getAsStream(arrayIndex);
            printStream(out, prefix, stream);
        } else {
            out.println(outputFirstPart + value.toString());
        }
    }

    private PdfObject resolveIfIndirect(PdfArray array, int arrayIndex, PdfObject value) {
        if (value.isIndirect()) {
            PdfIndirectReference indirect = array.getAsIndirectObject(arrayIndex);
            value = PdfReader.getPdfObject(indirect);
        }
        return value;
    }

    private void printStream(PrintStream out, String prefix, PdfStream stream) throws Exception {
        byte[] bytes;
        if (stream instanceof PRStream) {
            bytes = PdfReader.getStreamBytes((PRStream) stream);
        } else {
            bytes = stream.getBytes();
        }
        String content = (bytes != null ? new String(bytes) : null);
        out.println(prefix + SPACER + content);
    }
}
