package com.luka.anidroid.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.luka.anidroid.model.Anime;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<List<Anime>> searchResults = new MutableLiveData<>();

    public MutableLiveData<List<Anime>> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<Anime> animes) {
        searchResults.setValue(animes);
    }
}