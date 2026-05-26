package com.pphi.tower.service.context;

public class GuardiansContext implements ChatContext {

    private final String guardiansData;

    public GuardiansContext(String guardiansData) {
        this.guardiansData = guardiansData;
    }

    @Override
    public String getLabel() {
        return "Guardians";
    }

    @Override
    public String getContent() {
        return guardiansData;
    }
}
