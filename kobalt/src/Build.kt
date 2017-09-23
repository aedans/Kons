import com.beust.kobalt.plugin.publish.bintray
import com.beust.kobalt.project

val kotlinCons = project {
    name = "Kotlin-Cons"
    group = "io.github.aedans"
    artifactId = "kotlin-cons"
    version = "1.0"

    dependencies {
        compile("org.jetbrains.kotlin:kotlin-runtime:1.1.2")
        compile("org.jetbrains.kotlin:kotlin-stdlib:1.1.2")
    }

    dependenciesTest {
        compile("org.testng:testng:6.11")
    }

    bintray {
        publish = true
    }
}
