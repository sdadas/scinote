package com.sdadas.scinote.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

/**
 * @author Sławomir Dadas <sdadas@opi.org.pl>
 */
@RestController
public class FilesController {

    private final String storageDir;

    @Autowired
    public FilesController(@Value("${paper.parser.storageDir}") String storageDir) {
        this.storageDir = storageDir;
    }

    @GetMapping("/pdf/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        File baseDir = new File(storageDir);
        File file = new File(baseDir, fileName);
        if(!file.exists()) {
            return ResponseEntity.notFound().build();
        } else if(!file.getCanonicalPath().startsWith(baseDir.getCanonicalPath())) {
            return ResponseEntity.badRequest().build();
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
