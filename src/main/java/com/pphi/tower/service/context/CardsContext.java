package com.pphi.tower.service.context;

public class CardsContext implements ChatContext {

    private final String tableData;

    public CardsContext(String tableData) {
        this.tableData = tableData;
    }

    @Override
    public String getLabel() {
        return "Cards";
    }

    @Override
    public String getContent() {
        return tableData;
    }
}
