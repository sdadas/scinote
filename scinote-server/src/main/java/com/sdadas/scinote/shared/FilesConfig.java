package com.sdadas.scinote.shared;

import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@Component
@ConfigurationProperties(prefix = "files")
public class FilesConfig implements Serializable {

    private String storageDir;

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public File fileFromResource(Resource resource, String fileId) throws IOException {
        if (resource instanceof FileSystemResource) {
            return resource.getFile();
        } else {
            String filename = fileId + ".pdf";
            File output = new File(storageDir, filename);
            try(InputStream is = resource.getInputStream()) {
                FileUtils.forceMkdirParent(output);
                ByteSink sink = Files.asByteSink(output);
                sink.writeFrom(is);
                return output;
            }
        }
    }
}
