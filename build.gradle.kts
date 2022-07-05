import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlin_version = "1.6.20"
    kotlin("jvm") version kotlin_version
    kotlin("plugin.serialization") version kotlin_version
    application
}

group = "me.felix.ir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://gitlab.rz.uni-bamberg.de/api/v4/projects/4336/packages/maven")
        name = "MINF_private"
        credentials(HttpHeaderCredentials::class){
            name = "Deploy-Token"
            value = "vny-6sgSpFPMRQHspKJ-"
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}

val ktorVersion: String by project
val kTestFactoriesVersion: String by project
val jUnitJupiterVersion: String by project
val apacheCommonsCSVVersion: String by project
val poiVersion: String by project
val solrVersion: String by project

dependencies {
    //Stdlb
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation(kotlin("reflect"))

    // Kotlin Libs
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    //ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
//    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")


    // https://mvnrepository.com/artifact/org.apache.poi/poi
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("org.apache.solr:solr-solrj:$solrVersion")
// https://mvnrepository.com/artifact/org.apache.commons/commons-csv
    implementation("org.apache.commons:commons-csv:${apacheCommonsCSVVersion}")
    // Unit tests
    testImplementation("de.fengl.ktestfactories:ktestfactories:$kTestFactoriesVersion")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jUnitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitJupiterVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}


val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}
