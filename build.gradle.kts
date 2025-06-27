plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	idea
}

group = "br.com.study"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

sourceSets {
	create("integrationTest") {
		kotlin { srcDir("src/test-integration/kotlin") }
		resources { srcDir("src/test-integration/resources") }

		compileClasspath += sourceSets.main.get().output
		runtimeClasspath += sourceSets.main.get().output
	}
}

idea {
	module {
		testSources.from(sourceSets["integrationTest"].java.srcDirs)
	}
}

val integrationTestImplementation by configurations.getting {
	extendsFrom(configurations.implementation.get())
}
integrationTestImplementation.extendsFrom(configurations.testImplementation.get())

val integrationTestRuntimeOnly by configurations.getting
configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.mockk:mockk:1.14.4")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val integrationTest = tasks.register<Test>("integrationTest") {
	description = "Runs integration tests."
	group = "verification"
	testClassesDirs = sourceSets["integrationTest"].output.classesDirs
	classpath = sourceSets["integrationTest"].runtimeClasspath
	shouldRunAfter("test")

	useJUnitPlatform()

	testLogging {
		events("passed")
	}
}

tasks.check { dependsOn(integrationTest) }
