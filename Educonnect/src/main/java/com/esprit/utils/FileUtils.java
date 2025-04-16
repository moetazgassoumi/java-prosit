package com.esprit.utils;

import com.esprit.exceptions.ValidationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    public static void saveFile(File sourceFile, String destinationPath) throws ValidationException {
        try {
            Path destPath = Paths.get(destinationPath);
            Files.createDirectories(destPath.getParent());
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ValidationException("Failed to save file: " + e.getMessage());
        }
    }

    public static void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filePath);
        }
    }

    public static String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}