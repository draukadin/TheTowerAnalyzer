package com.pphi.tower.model.sheets.modules;

public record EquippedModule(Module primarySlot, Module assistSlot) {

    public String type() {
        return primarySlot().type();
    }
}
