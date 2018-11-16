package com.udacity.gradle.builditbigger;

// https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
public interface AsyncResponse {
    void onResult(String msg);
}
