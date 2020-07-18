package com.ulyp.agent.settings;

public class SettingsProperty<T> {

    private final String name;
    private volatile T value;

    public SettingsProperty(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SettingsProperty{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
