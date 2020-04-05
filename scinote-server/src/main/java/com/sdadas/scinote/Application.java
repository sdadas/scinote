package com.sdadas.scinote;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

/**
 * @author Sławomir Dadas
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource dataSource() throws IOException {
        File dir = new File("data");
        FileUtils.forceMkdir(dir);
        File db = new File(dir, "data");
        return JdbcConnectionPool.create("jdbc:h2:file:" + db.getAbsolutePath(), "sa", "");
    }
}
