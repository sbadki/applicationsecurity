package com.example.others;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Extract the archive file and address few security concerns
 * 1. To check whether there are too many files present in archive.
 * 2. To check whether file size is too big to extract from archive.
 * 3. To check the file path where it's extracting to is the correct target directory.
 *
 */
public class UnzipFile {

    static final String PATH = "src\\main\\resources\\";
    static final int BUFFER = 512;
    static final long TOOBIG = 0x6400000; // Max size of unzipped data, 100MB

    public static void main(final String[] args) throws IOException {
        final String fileName = "archive.zip";
        unZip(fileName);
    }

    private static void unZip(String fileName) throws IOException {
        final File destDir = new File(PATH + "extract");
        final byte[] buffer = new byte[1024];

        long total = 0;
        int entries = 0;

        try(final ZipInputStream zis = new ZipInputStream(new FileInputStream(PATH + fileName))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                final File newFile = ZipUtil.validateFileName(destDir, zipEntry.getName());
                System.out.println(newFile);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    try(final FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while (total + BUFFER <= TOOBIG && (len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                            total += len;
                        }
                    }
                    System.out.println(newFile + " Size :"+ total);
                    entries++;
                    ZipUtil.tooManyFilesToUnzip(entries);
                    ZipUtil.toCheckFileSize(total);
                    zipEntry = zis.getNextEntry();
                }
            }
            zis.closeEntry();
        };
    }
}