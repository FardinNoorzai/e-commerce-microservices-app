package shopmate.productservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shopmate.productservice.dto.ProductRequest;
import shopmate.productservice.models.Product;
import shopmate.productservice.repositories.ProductRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private static final String IMAGE_BASE_PATH = "/home/noorzai/uploads/images/";
    @Autowired
    ProductRepository productRepository;

    public Product saveProduct(Product product){
        return productRepository.save(product);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAllByIsActiveTrue(pageable);
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.findById(id);
    }
    public Product updateProduct(Integer id, Product updatedProduct) {
        return productRepository.findById(id).map(product -> {
            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            product.setBrand(updatedProduct.getBrand());
            product.setImageUrl(updatedProduct.getImageUrl());
            product.setMimeType(updatedProduct.getMimeType());
            product.setExtension(updatedProduct.getExtension());
            product.setIsActive(updatedProduct.getIsActive());
            product.setProductCategory(updatedProduct.getProductCategory());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public Product save(ProductRequest productRequest, MultipartFile image) throws IOException {
        Product product = new Product();
        String imageUrl = saveImage(image,product);
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(imageUrl);
        product.setIsActive(productRequest.getIsActive());
        product.setBrand(productRequest.getBrand());
        return productRepository.save(product);
    }

    private String saveImage(MultipartFile image,Product product) throws IOException {
        String mimeType = image.getContentType();
        if (mimeType == null) {
            throw new IOException("File MIME type is unknown");
        }
        String extension = getExtensionFromMimeType(mimeType);
        if (extension == null) {
            throw new IOException("Unsupported file type");
        }
        product.setMimeType(mimeType);
        product.setExtension(extension);
        String imageDirectory = System.getProperty("user.home") + "/uploads/images/";
        String name = UUID.randomUUID().toString();
        String imagePath = imageDirectory + name;
        Path dirPath = Paths.get(imageDirectory);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        image.transferTo(new java.io.File(imagePath));

        return name;
    }



    public ResponseEntity<FileSystemResource> getFile(String fileName) {
        Product product = productRepository.findByImageUrl(fileName);

        File file = new File(IMAGE_BASE_PATH + fileName);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        FileSystemResource resource = new FileSystemResource(file);

        String fileExtension = product.getExtension();
        MediaType mediaType = determineMediaType(fileExtension);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
    private MediaType determineMediaType(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/bmp":
                return "bmp";
            case "image/webp":
                return "webp";
            default:
                return null;
        }
    }

    public Optional<Product> findById(Integer id){
        return productRepository.findById(id);
    }

}
