package com.ulyp.agent.settings;

public class SettingsProperty<T> {

    private final String name;
    private T value;

    public SettingsProperty(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public boolean hasValue() {
        return value != null;
    }

    @Override
    public String toString() {
        return "SettingsProperty{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
