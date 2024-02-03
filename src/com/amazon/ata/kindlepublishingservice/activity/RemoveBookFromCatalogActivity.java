package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {
    private CatalogDao catalogDao; //CatalogDao to access the Catalog table
    @Inject
    // may have to put CatalogDao as a parameter
    RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }
    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        String bookId = removeBookFromCatalogRequest.getBookId();
        CatalogItemVersion catalogItem = catalogDao.removeBook(bookId);
//        if (catalogItem == null) {
//            throw new BookNotFoundException(bookId);
//        }

        return new RemoveBookFromCatalogResponse();
    }
}
