package com.squeezymo.mutibo.ui.activites;

import android.content.Intent;

public interface CallbackHandler {
    void pass(int resultCode);
    void pass(int resultCode, Intent intent);
}
