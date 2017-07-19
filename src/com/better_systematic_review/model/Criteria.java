package com.better_systematic_review.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Porter on 7/17/2017.
 */
public class Criteria {
    private SimpleStringProperty name;
    private SimpleStringProperty value;
    private SimpleBooleanProperty required;

    public Criteria(){
        name = new SimpleStringProperty("New");
        value = new SimpleStringProperty();
        required = new SimpleBooleanProperty();
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public boolean isRequired() {
        return required.get();
    }

    public SimpleBooleanProperty requiredProperty() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required.set(required);
    }
}
