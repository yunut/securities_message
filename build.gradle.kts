import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	kotlin("plugin.serialization") version "1.9.24"

}

group = "com.catches"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
	maven(url = "https://jitpack.io")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// spring boot configuration annotation
	annotationProcessor(
		"org.springframework.boot:spring-boot-configuration-processor",
	)

	// telegram bot
	implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")

	// retrofit2 + xml
	implementation("com.squareup.retrofit2:retrofit:2.9.0")
	implementation(
		"com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0",
	)
	implementation("com.squareup.okhttp3:logging-interceptor")
	implementation("com.squareup.retrofit2:converter-jackson:2.9.0")

	// for json(need for kotlin data class parsing)
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-serialization:1.6.21")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
	implementation("jakarta.json:jakarta.json-api:2.0.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

	// kotest
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
	testImplementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
	testImplementation("io.mockk:mockk:1.13.3")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
	testImplementation("com.appmattus.fixture:fixture-kotest:1.2.0")
	testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.1")

	// test container
	testImplementation("org.testcontainers:testcontainers:1.18.3")
	testImplementation("org.testcontainers:mysql:1.18.3")

	// validation
	implementation("org.springframework.boot:spring-boot-starter-validation")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
