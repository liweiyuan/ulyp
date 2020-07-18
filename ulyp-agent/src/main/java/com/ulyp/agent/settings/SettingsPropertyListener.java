package com.ulyp.agent.settings;

public interface SettingsPropertyListener<T> {

    void onValueChanged(T oldValue, T newValue);
}
