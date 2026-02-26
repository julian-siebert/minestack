plugins {
    `java-library`
    signing
    id ("org.danilopianini.publish-on-central")
}

dependencies {
    compileOnly(libs.paperApi)

    compileOnlyApi(libs.mongo)
    compileOnlyApi(libs.hazelcast)
    compileOnlyApi(libs.guice)

    compileOnlyApi(libs.lombok)
    annotationProcessor(libs.lombok)
}

publishOnCentral {
    projectLongName.set("minestack")
    projectDescription.set(project.description)
    licenseName = "Apache License, Version 2.0"
    licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
    projectUrl = "https://minestack.minetrix.eu/"
    scmConnection = "scm:git:https://codeberg.org/minetrix/minestack"
}

publishing.publications.withType<MavenPublication> {
    pom {
        developers {
            developer {
                name = "Julian Siebert"
                email = "mail@julian-siebert.de"
                url = "https://www.julian-siebert.de/"
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("MAVEN_CENTRAL_GPG_KEY"),
        System.getenv("MAVEN_CENTRAL_GPG_PASSWORD")
    )
}