package com.example.others;

import java.io.File;
import java.io.IOException;

/**
 *
 *  Following security flaws are taken care in this library
 *
 *  1. No of file to be extract are too many
 *  2. Size of the file extracted is too big could cause zip bomb
 *  3. File names in zip archives can contain path traversal information. When extracted, this will lead to files being
 *  *  created outside intended directory.
 */
public class ZipUtil {

    static final int TOOMANY = 1024;      // Max number of files
    static final int BUFFER = 512;
    static final long TOOBIG = 0x6400000; // Max size of unzipped data, 100MB

    public static void tooManyFilesToUnzip(int entries) {
        if (entries > TOOMANY) {
            throw new IllegalStateException("Too many files to unzip.");
        }
    }

    public static void toCheckFileSize(Long total) {
        if (total + BUFFER > TOOBIG) {
            throw new IllegalStateException("File being unzipped is too big.");
        }
    }

    public static File validateFileName(File destinationDir, String fileName) throws IOException {
        File destFile = new File(destinationDir, fileName);

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + fileName);
        }

        return destFile;
    }

}
