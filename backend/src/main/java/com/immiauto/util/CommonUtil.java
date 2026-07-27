package com.immiauto.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

/**
 * Shared file-handling helpers (validation, filename sanitizing, MIME detection).
 * Centralised here so upload flows (documents, generated/uploaded forms) reuse the
 * same rules instead of duplicating them.
 */
public final class CommonUtil {

    private CommonUtil() {
    }

    /** Magic-number signatures for the formats we can verify. */
    private static final Map<String, byte[]> FILE_SIGNATURES = Map.of(
            "pdf", new byte[]{0x25, 0x50, 0x44, 0x46},
            "jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}
    );

    /**
     * Validate an uploaded file: non-empty, within size, allowed extension, and
     * (for known types) a matching magic-number signature.
     */
    public static void validateUpload(MultipartFile file, Set<String> allowedExtensions,
                                      long maxSizeBytes) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException(
                    "File size exceeds maximum allowed size of " + (maxSizeBytes / 1024 / 1024) + "MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("File must have a name");
        }
        String extension = getExtension(originalName).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("File type '" + extension + "' is not allowed. Accepted: "
                    + String.join(", ", allowedExtensions));
        }
        verifyFileSignature(file, extension);
    }

    private static void verifyFileSignature(MultipartFile file, String extension) throws IOException {
        byte[] signature = FILE_SIGNATURES.get(extension);
        if (signature == null) {
            return;
        }
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[signature.length];
            int read = is.read(header);
            if (read < signature.length) {
                throw new IllegalArgumentException("File appears to be corrupt or too small");
            }
            for (int i = 0; i < signature.length; i++) {
                if (header[i] != signature[i]) {
                    throw new IllegalArgumentException(
                            "File content does not match its extension '" + extension + "'");
                }
            }
        }
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null) return "unnamed";
        String name = Paths.get(filename).getFileName().toString();
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (name.startsWith(".")) {
            name = "_" + name;
        }
        if (name.length() > 255) {
            String ext = getExtension(name);
            name = name.substring(0, 250) + "." + ext;
        }
        return name;
    }

    public static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1);
    }

    public static String detectMimeType(String filename) {
        String extension = getExtension(filename == null ? "" : filename).toLowerCase();
        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "tif", "tiff" -> "image/tiff";
            case "bmp" -> "image/bmp";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt" -> "text/plain";
            case "rtf" -> "application/rtf";
            case "odt" -> "application/vnd.oasis.opendocument.text";
            case "ods" -> "application/vnd.oasis.opendocument.spreadsheet";
            default -> "application/octet-stream";
        };
    }
}
