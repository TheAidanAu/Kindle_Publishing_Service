package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;

public class RemoveBookFromCatalogActivity {
    private CatalogDao catalogDao; //CatalogDao to access the Catalog table
    @Inject
    // may have to put CatalogDao as a parameter
    RemoveBookFromCatalogActivity() {}
    public RemoveBookFromCatalogResponse execute(RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        return null;
    }
}
