package com.perkelle.dev.envoys.dependencyinjection;


import com.perkelle.dev.envoys.Bootstrap;
import me.lucko.jarrelocator.Relocation;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Pattern {
    private final String encoded;
    public final SubstitutionType substitutionType;

    public Pattern(String encoded, SubstitutionType substitutionType) {
        this.encoded = encoded;
        this.substitutionType = substitutionType;
    }

    public String getRawPattern() {
        if (substitutionType == SubstitutionType.BASE64) {
            return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } else if (substitutionType == SubstitutionType.CURLY_BRACE) {
            return encoded.replace("{}", ".");
        } else {
            throw new IllegalStateException("Enum exhausted");
        }
    }

    public String getRelocatedPattern() {
        return String.format("com.perkelle.dev.envoys.dependencies.%s", getRawPattern());
    }

    public Relocation getRelocation() {
        return new Relocation(getRawPattern(), getRelocatedPattern());
    }
}