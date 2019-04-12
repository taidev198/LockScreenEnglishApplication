package com.framgia.lockscreenenglishapplication.service;

import android.support.annotation.StringDef;

import static com.framgia.lockscreenenglishapplication.service.Action.CLOSE;
import static com.framgia.lockscreenenglishapplication.service.Action.NEXT;
import static com.framgia.lockscreenenglishapplication.service.Action.TOGGLE_STATE;

@StringDef({TOGGLE_STATE, NEXT, CLOSE})
public @interface Action {
    String TOGGLE_STATE = "TOGGLE_STATE";
    String NEXT = "NEXT";
    String CLOSE = "CLOSE";
}
