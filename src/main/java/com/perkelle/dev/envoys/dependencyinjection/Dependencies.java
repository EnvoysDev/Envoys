package com.perkelle.dev.envoys.dependencyinjection;

import com.perkelle.dev.dependencymanager.dependency.Dependency;
import com.perkelle.dev.dependencymanager.dependency.impl.nexus.MavenCentralDependency;
import org.bukkit.plugin.Plugin;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Dependencies {
    public List<Dependency> getDependencies(Plugin pl) {
        return Arrays.asList(
                new MavenCentralDependency(pl, dots("org{}jetbrains{}kotlin"), "kotlin-stdlib", "1.5.20"),
        /*
        NexusDependency(Envoys.instance, "https://repo.codemc.org/repository/maven-public/", "org{}codemc{}worldguardwrapper".dots(), "worldguardwrapper", "1.1.6-SNAPSHOT")
                .setRelocation(Pattern("org{}codemc{}worldguardwrapper", SubstitutionType.CURLY_BRACE).getRelocation()),
         */

                new MavenCentralDependency(pl, dots("xyz{}xenondevs"), "particle", "1.5.1"),
                //.setRelocation(new Pattern("xyz{}xenondevs", SubstitutionType.CURLY_BRACE).getRelocation()),

                new MavenCentralDependency(pl, "rhino", "js", "1.7R2"),

        /*
        NexusDependency(Envoys.instance, "https://repo.codemc.org/repository/maven-public/", "de{}tr7zw".dots(), "item-nbt-api-plugin", "2.6.0")
                .setRelocation(Pattern("de{}tr7zw", SubstitutionType.CURLY_BRACE).getRelocation()),
         */

                new MavenCentralDependency(pl, dots("org{}json"), "json", "20190722"),
                //.setRelocation(new Pattern("org{}json", SubstitutionType.CURLY_BRACE).getRelocation()),

                new MavenCentralDependency(pl, dots("me{}lucko"), "commodore", "1.10"),
                //.setRelocation(new Pattern("me{}lucko", SubstitutionType.CURLY_BRACE).getRelocation()),

                // commons-codec:commons-codec
                new MavenCentralDependency(pl, b64("Y29tbW9ucy1jb2RlYw=="), b64("Y29tbW9ucy1jb2RlYw=="), "1.11"),
                //.setRelocation(new Pattern("Y29tbW9ucy1jb2RlYw==", SubstitutionType.BASE64).getRelocation()),

                // commons-logging:commons-logging
                new MavenCentralDependency(pl, b64("Y29tbW9ucy1sb2dnaW5n"), b64("Y29tbW9ucy1sb2dnaW5n"), "1.2"),
                //.setRelocation(new Pattern("Y29tbW9ucy1sb2dnaW5n", SubstitutionType.BASE64).getRelocation()),

                new MavenCentralDependency(pl, dots("org{}apache{}httpcomponents"), "httpcore", "4.4.14"),
                //.setRelocation(new Pattern("org{}apache{}httpcomponents", SubstitutionType.CURLY_BRACE).getRelocation()),

                new MavenCentralDependency(pl, dots("org{}apache{}httpcomponents"), "httpclient", "4.5.13"),
                //.setRelocation(new Pattern("org{}apache{}httpcomponents", SubstitutionType.CURLY_BRACE).getRelocation()),

                new MavenCentralDependency(pl, dots("org{}apache{}httpcomponents"), "fluent-hc", "4.5.13"),
                //.setRelocation(new Pattern("org{}apache{}httpcomponents", SubstitutionType.CURLY_BRACE).getRelocation()),

        /*
        NexusDependency(Envoys.instance, "https://papermc.io/repo/repository/maven-public/", "io{}papermc".dots(), "paperlib", "1.0.6")
                .setRelocation(Pattern("io{}papermc", SubstitutionType.CURLY_BRACE).getRelocation()),
         */

                new MavenCentralDependency(pl, dots("org{}apache{}commons"), "commons-math3", "3.6.1")
                //.setRelocation(new Pattern("org{}apache{}commons{}math3", SubstitutionType.CURLY_BRACE).getRelocation())
        );
    }

    private String dots(String s) {
        return s.replace("{}", ".");
    }

    private String b64(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }
}
