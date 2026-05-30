package com.example.thelibrarymanagementsystem.model;

public enum InstitutionType {
    SCHOOL("School"),
    COLLEGE("College"),
    UNIVERSITY("University");

    private final String label;

    InstitutionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static InstitutionType fromLabel(String value) {
        for (InstitutionType type : values()) {
            if (type.label.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNIVERSITY;
    }
}
