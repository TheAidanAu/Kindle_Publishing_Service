package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishing.utils.KindleConversionUtils;
import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import javax.inject.Inject;

public class BookPublishTask implements Runnable {
    private PublishingStatusDao publishingStatusDao;
    private BookPublishRequestManager bookPublishRequestManager;
    private CatalogDao catalogDao;

    @Inject
    public BookPublishTask(PublishingStatusDao publishingStatusDao,
            BookPublishRequestManager bookPublishRequestManager,
                           CatalogDao catalogDao) {
        this.publishingStatusDao = publishingStatusDao;
        this.bookPublishRequestManager = bookPublishRequestManager;
        this.catalogDao = catalogDao;
    }

    /*
    Process a BookPublishRequest from BookPublishRequestManager
    and publish a book to the Catalog
     */
    @Override
    public void run() {

        // For each BookPublishRequest in the queue, we do some tasks to publish a book in the catalog
        // Retrieve a BookPublishRequest from the BookPublishRequestManager
        BookPublishRequest bookPublishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();

        // if bookPublishRequestManager doesn't have any publish requests,
        // BookPublishTask should return immediately without taking any action
        if (bookPublishRequest == null) {
            return;
        }

        // process publish requests from the bookPublishRequestManager
        // Add a PublishingStatusItem to the table with the IN_PROGRESS status
        publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                PublishingRecordStatus.IN_PROGRESS,
                bookPublishRequest.getBookId()
        );
        // performs formatting and conversion of the book
        KindleFormattedBook formattedBook = KindleFormatConverter.format(bookPublishRequest);

        CatalogItemVersion newBookInCatalog;

        try {
            newBookInCatalog = this.createOrUpdate(formattedBook);
        } catch (BookNotFoundException ex) {
            // if an exception is caught while processing
            // add a FAILED status PublishingStatusItem to the table
            // and include the exception message
            publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                    PublishingRecordStatus.FAILED,
                    bookPublishRequest.getBookId(),
                    ex.getMessage());
            return;
        }

            // Add an item to the PublishingStatus table if all the previous steps succeed.
        publishingStatusDao.setPublishingStatus(bookPublishRequest.getPublishingRecordId(),
                PublishingRecordStatus.SUCCESSFUL,
                newBookInCatalog.getBookId()
                );
    }

    // This is from the Sequence Diagram
    public CatalogItemVersion createOrUpdate(KindleFormattedBook formattedBook) {
        // if there's NO bookId in the request, it's considered a new book
        // we will add a new book to the catalog
        // if there's a bookId in the request, we will attempt to update an existing book in the catalog
        CatalogItemVersion book;
        if (formattedBook.getBookId() == null) {
            book = catalogDao.create(formattedBook);
        } else {
            book = catalogDao.update(formattedBook);
        }

        return book;
    }
}
