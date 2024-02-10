package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/*
Save each book publish request as a collection into this class.
The requests will be processed later in the order submitted - FIFO.
asynchronous processing
 */

@Singleton
public class BookPublishRequestManager {
    //Using a ConcurrentLinkedQueue to make it thread-safe
    // So that multiple threads can write to and read from the queue
    private Queue<BookPublishRequest> bookPublishRequests = new ConcurrentLinkedDeque<>();


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
