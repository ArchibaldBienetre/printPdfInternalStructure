package org.example;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsageAndExit();
        }
        String filePath = args[0];
        PdfStructurePrinter pdfStructurePrinter = new PdfStructurePrinter();
        pdfStructurePrinter.print(filePath, System.out);
    }

    private static void printUsageAndExit() {
        System.out.println("\n################################\nUsage: [program] filePath\n################################\n");
        System.exit(1);
    }
}
