plugins {
    id("com.gradleup.shadow") version "9.3.1" apply false
    id("org.cthing.build-constants") version "2.1.0" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2" apply false
    id("xyz.jpenilla.resource-factory") version "1.3.1" apply false
    id ("org.danilopianini.publish-on-central") version "9.1.13" apply false
}

allprojects {
    group = "eu.minetrix"
    description = "Minestack framework"
    version = "0.0.0"

    repositories {
        mavenCentral()
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}
