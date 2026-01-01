import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "2.2.21"

    java
    application
    idea
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("./lib/neptune-serverscript-compiler-0.0.1-SNAPSHOT.jar"))
    implementation("io.github.nullpops:eventbus:1.0.1")
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.slf4j:slf4j-nop:2.0.16")
    implementation("com.lmax:disruptor:3.4.2")
    implementation("io.netty:netty-all:4.2.5.Final")
    implementation("com.google.guava:guava:33.4.8-jre")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.github.classgraph:classgraph:4.8.179")
    implementation(kotlin("script-runtime"))
    implementation(kotlin("reflect"))
    implementation(kotlin("scripting-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.mockito:mockito-core:5.14.2")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.apache.commons:commons-compress:1.27.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

group = "luna"
version = "1.0"

application {
    mainClass = "io.luna.Luna"
    applicationDefaultJvmArgs = listOf(
        "--add-opens=java.base/java.time=ALL-UNNAMED"
    )
}

java {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23

    sourceSets {
        main {
            java.srcDirs("src/main/java")
        }
    }
}

kotlin {
    sourceSets {
        main {
            kotlin.setSrcDirs(emptyList<Any>())
            kotlin.srcDirs(
                "src/main/java",
                "src/main/kotlin/"
            )
        }
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs = MutableList(1) { "-Xlint:unchecked" }
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
