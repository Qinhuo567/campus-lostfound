package com.campus.lostfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class LostFoundApplication {

    public static void main(String[] args) {
        try {
            Files.createDirectories(Path.of("data"));
            Files.createDirectories(Path.of("uploads"));
        } catch (IOException e) {
            throw new RuntimeException("无法创建数据目录", e);
        }
        SpringApplication.run(LostFoundApplication.class, args);
    }
}
