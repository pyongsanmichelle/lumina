package com.example.api.infrastructure.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * メッセージプロパティファイルの整合性検証テスト
 * 
 * ローカライズファイル（messages_*.properties）とデフォルトファイル（messages.properties）の
 * キー定義の同期ズレを検出する
 */
@DisplayName("メッセージプロパティファイルの整合性検証")
class MessagePropertiesTest {

    private static final String DEFAULT_MESSAGES_FILE = "messages.properties";
    private static final String RESOURCES_PATH = "src/main/resources/";

    @Test
    @DisplayName("全てのローカライズファイルのキーがデフォルトファイルに存在する")
    void 全てのローカライズファイルのキーがデフォルトファイルに存在する() throws IOException {
        // デフォルトファイルのキーを取得
        Set<String> defaultKeys = loadKeysFromResources(DEFAULT_MESSAGES_FILE);
        
        // messages_*.properties ファイルをスキャン
        Set<String> allMessageFiles = findAllMessagePropertiesFiles();
        
        Assertions.assertThat(allMessageFiles)
            .as("メッセージプロパティファイルが1つ以上存在すること")
            .isNotEmpty();

        for (String messageFile : allMessageFiles) {
            if (DEFAULT_MESSAGES_FILE.equals(messageFile)) {
                continue; // デフォルトファイルはスキップ
            }

            Set<String> localeKeys = loadKeysFromResources(messageFile);
            Set<String> missingKeys = findMissingKeys(defaultKeys, localeKeys);
            
            Assertions.assertThat(missingKeys)
                .as("%s に存在するが %s に存在しないキー: %s", messageFile, DEFAULT_MESSAGES_FILE, missingKeys)
                .isEmpty();
        }
    }

    @Test
    @DisplayName("デフォルトファイルのキーが全てのローカライズファイルに存在する")
    void デフォルトファイルのキーが全てのローカライズファイルに存在する() throws IOException {
        Set<String> defaultKeys = loadKeysFromResources(DEFAULT_MESSAGES_FILE);
        Set<String> allMessageFiles = findAllMessagePropertiesFiles();
        
        for (String messageFile : allMessageFiles) {
            if (DEFAULT_MESSAGES_FILE.equals(messageFile)) {
                continue;
            }

            Set<String> localeKeys = loadKeysFromResources(messageFile);
            Set<String> missingKeys = findMissingKeys(localeKeys, defaultKeys);
            
            Assertions.assertThat(missingKeys)
                .as("%s に存在するが %s に存在しないキー: %s", DEFAULT_MESSAGES_FILE, messageFile, missingKeys)
                .isEmpty();
        }
    }

    /**
     * プロパティファイルからキーのみを抽出
     */
    private Set<String> loadKeysFromResources(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);
        
        if (!resource.exists()) {
            return new LinkedHashSet<>();
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), "UTF-8"))) {
            
            return reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(line -> line.split("=", 2)[0].trim())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }

    /**
     * src/main/resources/ 配下の全ての messages_*.properties ファイルを検索
     */
    private Set<String> findAllMessagePropertiesFiles() throws IOException {
        Set<String> files = new LinkedHashSet<>();
        files.add(DEFAULT_MESSAGES_FILE);
        
        // ClassPathから全リソースを取得
        ClassPathResource resourcesDir = new ClassPathResource("");
        
        if (resourcesDir.exists() && resourcesDir.getFile().isDirectory()) {
            java.io.File[] filesInDir = resourcesDir.getFile().listFiles();
            
            if (filesInDir != null) {
                for (java.io.File file : filesInDir) {
                    String fileName = file.getName();
                    if (fileName.matches("messages_.*\\.properties")) {
                        files.add(fileName);
                    }
                }
            }
        }
        
        return files;
    }

    /**
     * expectedKeys に存在するが actualKeys に存在しないキーを抽出
     */
    private Set<String> findMissingKeys(Set<String> expectedKeys, Set<String> actualKeys) {
        Set<String> missingKeys = new LinkedHashSet<>();
        
        for (String key : expectedKeys) {
            if (!actualKeys.contains(key)) {
                missingKeys.add(key);
            }
        }
        
        return missingKeys;
    }
}
