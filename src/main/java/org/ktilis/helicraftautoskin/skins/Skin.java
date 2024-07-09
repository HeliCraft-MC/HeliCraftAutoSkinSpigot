package org.ktilis.helicraftautoskin.skins;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
public class Skin {
    private final String value;
    private final String signature;
    @Nullable private final String skinName;

    public Skin(String value, String signature) {
        this.value = value;
        this.signature = signature;
        this.skinName = null;
    }
}
