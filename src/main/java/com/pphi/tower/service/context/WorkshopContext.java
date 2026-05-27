package com.pphi.tower.service.context;

public class WorkshopContext implements ChatContext {

    private final String workshopData;

    public WorkshopContext(String workshopData) {
        this.workshopData = workshopData;
    }

    @Override
    public String getLabel() {
        return "Workshop";
    }

    @Override
    public String getContent() {
        return workshopData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
