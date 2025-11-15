package com.example.stayconnected.property.enums;

public enum CategoryType {
    APARTMENT("Apartment"),
    HOUSE("House"),
    CABIN("Cabin"),
    VILLA("Villa"),
    STUDIO("Studio"),
    TOWNHOUSE("Townhouse");

    private String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
