plugins {
    `java-library`
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://clojars.org/repo/")
    }
}

dependencies {

    // last LGPL release of itext is version 4.2.0
//  https://mvnrepository.com/artifact/itext/itext
    implementation("itext:itext:4.2.0")
}

tasks.register<JavaExec>("runWithJavaExec") {
    group = "Execution"
    description = "Run the main class"
    classpath = sourceSets.main.get().runtimeClasspath
    main = "org.example.Main"
}
