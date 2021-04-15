package com.example.smarttrip.utils;

import com.google.firebase.database.DataSnapshot;

public interface IOnGetDataListener {
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
