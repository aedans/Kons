import com.beust.kobalt.plugin.packaging.assemble
import com.beust.kobalt.plugin.publish.bintray
import com.beust.kobalt.project

val kons = project {
    name = "kons"
    group = "io.github.aedans"
    artifactId = name
    version = "4.0.1"

    dependencies {
        compile("org.jetbrains.kotlin:kotlin-runtime:1.1.2")
        compile("org.jetbrains.kotlin:kotlin-stdlib:1.1.2")
        compile("io.arrow-kt:arrow-core:0.6.1")
    }

    dependenciesTest {
        compile("org.testng:testng:6.11")
    }

    assemble {
        mavenJars {
        }
    }

    bintray {
        publish = true
    }
}
