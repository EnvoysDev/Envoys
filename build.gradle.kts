import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.20"
    java
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.perkelle.dev.envoys"
version = "5.8.0"

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.codemc.org/repository/maven-public/")
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven(url = "https://maven.sk89q.com/repo")
    maven(url = "https://libraries.minecraft.net")
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    //implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    compileOnly(kotlin("stdlib"))
    compileOnly("com.github.Dot-Rar:DependencyManager:1.0.8")
    implementation("org.bstats:bstats-bukkit:3.0.0")

    // nexus repos
    implementation("org.codemc.worldguardwrapper:worldguardwrapper:1.1.6-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api-plugin:2.8.0")
    implementation("io.papermc:paperlib:1.0.7")

    //compile "org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT"
    //compileOnly("io.papermc.paper:paper-api:1.17-R0.1-SNAPSHOT")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.8.2")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.0")

    compileOnly("com.github.TechFortress:GriefPrevention:16.18") {
        exclude("com.sk89q.worldedit", "worldedit-bukkit")
        exclude("com.sk89q", "worldguard")
        exclude("org.bukkit", "bukkit")
    }
    compileOnly("rhino:js:1.7R2")
    compileOnly("org.json:json:20220924")
    compileOnly("xyz.xenondevs:particle:1.7.1")
    compileOnly("me.lucko:commodore:2.2")

    compileOnly("org.apache.httpcomponents:fluent-hc:4.5.13")
    compileOnly("org.apache.commons:commons-math3:3.6.1")
}

tasks {
    shadowJar {
        exclude("META-INF/**")
        exclude("proguard_map.txt")
        exclude("proguard_seeds.txt")
        exclude("module-info.class")
        exclude("LICENSE")

        archiveBaseName.set("Envoys")

        relocate("org.codemc.worldguardwrapper", "com.perkelle.dev.envoys.dependencies.org.codemc.worldguardwrapper")
        relocate("com.github.CodeMC", "com.perkelle.dev.envoys.dependencies.com.github.CodeMC")
        relocate("org.bstats", "com.perkelle.dev.envoys.dependencies.org.bstats")
        //relocate("org.intellij.lang.annotations", "com.perkelle.dev.envoys.dependencies.org.intellij.lang.annotations")
        //relocate("org.jetbrains.annotations", "com.perkelle.dev.envoys.dependencies.org.jetbrains.annotations")
        //relocate("xyz.xenondevs", "com.perkelle.dev.envoys.dependencies.xyz.xenondevs")
        relocate("de.tr7zw", "com.perkelle.dev.envoys.dependencies.de.tr7zw")
        relocate("me.lucko.jarrelocator", "com.perkelle.dev.envoys.dependencies.me.lucko.jarrelocator")
        //relocate("org.apache.httpcomponents", "com.perkelle.dev.envoys.dependencies.org.apache.httpcomponents")
        //relocate("commons-codec", "com.perkelle.dev.envoys.dependencies.commons-codec")
        //relocate("commons-logging", "com.perkelle.dev.envoys.dependencies.commons-logging")
        relocate("io.papermc", "com.perkelle.dev.envoys.dependencies.io.papermc")
        //relocate("org.apache.commons.math3", "com.perkelle.dev.envoys.dependencies.org.apache.commons.math3")
    }
}