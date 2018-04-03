package com.aikya.konnek2.utils.listeners;

public interface SearchListener {

    void prepareSearch();

    void search(String searchQuery);

    void cancelSearch();
}