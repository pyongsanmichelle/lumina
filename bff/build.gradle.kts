plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.4.0"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "BFF for Lumina"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    // テスト実行後にJaCoCoレポート生成とカバレッジ検証を自動実行
    finalizedBy(tasks.named("jacocoTestReport"), tasks.named("jacocoTestCoverageVerification"))
}

// JaCoCo設定: カバレッジレポートを build/reports/jacoco/test/html/index.html に出力
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}

// JaCoCoカバレッジ検証（品質ゲート）
// TODO: 初期の縦通しフェーズのため暫定50%。Controller/Service実装完了後に80%へ引き上げること
tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(tasks.named("test"))
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = "0.5".toBigDecimal()
            }
        }
    }
}

// build時にカバレッジ検証が自動実行されるよう依存関係を設定
tasks.named("check") {
    dependsOn(tasks.named("jacocoTestCoverageVerification"))
}