package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.Queue;

/*
Save each book publish request as a collection into this class.
The requests will be processed later in the order submitted - FIFO.
asynchronous processing
 */

@Singleton
public class BookPublishRequestManager {

    private Queue<BookPublishRequest> bookPublishRequests = new LinkedList<>();

    @Inject
    public BookPublishRequestManager() {

    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        bookPublishRequests.offer(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        return bookPublishRequests.poll();
    }

}
