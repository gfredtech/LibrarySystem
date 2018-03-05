package org.user_interface.ui;

import org.telegram.telegrambots.api.objects.Update;

public interface Executor {
    void processUpdate(Update update);
    boolean isDone();
}
