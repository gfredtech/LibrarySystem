package org.user_interface.commands;

import org.storage.LibraryStorage;
import org.storage.QueryParameters;
import org.storage.resources.PendingRequestEntry;
import org.storage.resources.Resource;
import org.storage.resources.UserEntry;

import java.util.List;

public class NotificationHandler {

    public String init(int userId) {
        List<PendingRequestEntry> entry = LibraryStorage.getInstance().find(Resource.PendingRequest,
                new QueryParameters().add("user_id", userId));

        StringBuilder builder = new StringBuilder();
        int i = 1;
        for(PendingRequestEntry e: entry) {
            String message = itemAvailableToCheckout(e);
               if(message != null) {
                   builder.append(i).append(". ").append(message);
                   i += 1;
               }
        }
        return builder.toString();

    }

    private String itemAvailableToCheckout(PendingRequestEntry entry) {
        if(entry.isOutstanding()) return "Checking out for " + entry.getItem().getItem().getTitle() + " will " +
                "not be available due to an outstanding request";
        else if(isAvailableForUser(entry))
        return entry.getItem().getItem().getTitle() + " available for checkout";
        else return null;
    }

    private boolean isAvailableForUser(PendingRequestEntry entry) {
        List<UserEntry> userEntries =
                LibraryStorage.getInstance().getQueueFor(entry.getItem());
        final UserEntry topQueryUser = userEntries.get(0);
        return topQueryUser.getId() == entry.getUser().getId();
    }
}
