package com.danydandy.SalonSpa.infrastructure.storage;

import com.danydandy.SalonSpa.config.properties.UploadProperties;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

@Component
public class FileStorageService {

    private static final Map<String, String> ALLOWED_CONTENT_TYPES = Map.of(
            MediaType.IMAGE_PNG_VALUE, "png",
            MediaType.IMAGE_JPEG_VALUE, "jpg",
            "image/webp", "webp"
    );

    private final Path uploadsDir;

    public FileStorageService(UploadProperties uploadProperties) {
        this.uploadsDir = Path.of(uploadProperties.dir()).toAbsolutePath().normalize();
    }

    public Mono<String> storeSalonLogo(Long salonId, FilePart filePart) {
        String contentType = filePart.headers().getContentType() != null
                ? filePart.headers().getContentType().toString()
                : null;
        String extension = ALLOWED_CONTENT_TYPES.get(contentType);

        if (extension == null) {
            return Mono.error(new BadRequestException(
                    "Formato de imagen no soportado. Usa PNG, JPG o WEBP."));
        }

        return Mono.fromCallable(() -> {
                    Files.createDirectories(uploadsDir);
                    removeExistingFiles("salon-" + salonId + "-logo.");
                    return uploadsDir.resolve(logoFileName(salonId, extension));
                })
                .flatMap(target -> filePart.transferTo(target).thenReturn(target))
                .map(target -> "/uploads/" + target.getFileName() + "?v=" + System.currentTimeMillis());
    }

    public Mono<Void> deleteSalonLogo(Long salonId) {
        return Mono.fromRunnable(() -> {
            try {
                removeExistingFiles("salon-" + salonId + "-logo.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Mono<String> storeProductImage(Long productId, FilePart filePart) {
        String contentType = filePart.headers().getContentType() != null
                ? filePart.headers().getContentType().toString()
                : null;
        String extension = ALLOWED_CONTENT_TYPES.get(contentType);

        if (extension == null) {
            return Mono.error(new BadRequestException(
                    "Formato de imagen no soportado. Usa PNG, JPG o WEBP."));
        }

        return Mono.fromCallable(() -> {
                    Files.createDirectories(uploadsDir);
                    removeExistingFiles(productImagePrefix(productId));
                    return uploadsDir.resolve(productImageFileName(productId, extension));
                })
                .flatMap(target -> filePart.transferTo(target).thenReturn(target))
                .map(target -> "/uploads/" + target.getFileName() + "?v=" + System.currentTimeMillis());
    }

    public Mono<Void> deleteProductImage(Long productId) {
        return Mono.fromRunnable(() -> {
            try {
                removeExistingFiles(productImagePrefix(productId));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void removeExistingFiles(String prefix) throws IOException {
        if (!Files.isDirectory(uploadsDir)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadsDir, prefix + "*")) {
            for (Path existing : stream) {
                Files.deleteIfExists(existing);
            }
        }
    }

    private String logoFileName(Long salonId, String extension) {
        return "salon-" + salonId + "-logo." + extension;
    }

    private String productImagePrefix(Long productId) {
        return "product-" + productId + "-image.";
    }

    private String productImageFileName(Long productId, String extension) {
        return productImagePrefix(productId) + extension;
    }

    public static Set<String> allowedContentTypes() {
        return ALLOWED_CONTENT_TYPES.keySet();
    }
}
