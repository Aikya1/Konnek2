package com.android.konnek2.call.core.core.command;

import android.os.Bundle;

public interface Command {

    void execute(Bundle bundle) throws Exception;
}