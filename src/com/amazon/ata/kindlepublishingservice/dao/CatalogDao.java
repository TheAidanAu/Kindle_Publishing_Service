package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;

import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the ACTIVE and latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Retrieves the latest version of a book from the CatalogItemVersions table
    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        // This is to specify the query parameters.
        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);
        //withHashKeyValues(book): Sets the hash key value for the query (filtering by bookId).
        //withScanIndexForward(false): Sets the scan direction to descending order (latest version first).
        //withLimit(1): Limits the query result to only one item (the latest version).

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    //TODO: update CatalogDao to implement the soft delete functionality (DONE)
    // then use it in the RemoveBookFromCatalogActivity class (DONE)
    public CatalogItemVersion removeBook(String bookId) {
        // mark the latest book's inactive attribute as true
        // only the latest book is active
        // use an existing method in the same class to get the latest book
        // load and save
        CatalogItemVersion latestBook = this.getBookFromCatalog(bookId);
        latestBook.setInactive(true);
        dynamoDbMapper.save(latestBook);
        return latestBook;
    }

    /*
    Check if a Book ID exists in the catalog.
    A book can either be active or inactive
     */
    public void validateBookExists(String bookId) {
        CatalogItemVersion bookToValidate = this.getLatestVersionOfBook(bookId);
        if (bookToValidate == null) {
            throw new BookNotFoundException(String.format("The book with the Book ID %s does not exist in the catalog", bookId));
        }
    }

    public CatalogItemVersion create(KindleFormattedBook formattedBook) {
        // publish a new book to the Catalog
        // Generate a new bookId for a new book
        String bookId = KindlePublishingUtils.generateBookId();
        CatalogItemVersion newBook = new CatalogItemVersion();
        newBook.setVersion(1);
        newBook.setInactive(false);
        newBook.setBookId(bookId);
        newBook.setAuthor(formattedBook.getAuthor());
        newBook.setGenre(formattedBook.getGenre());
        newBook.setText(formattedBook.getText());
        newBook.setTitle(formattedBook.getTitle());
        dynamoDbMapper.save(newBook);
        return newBook;
    }

    public CatalogItemVersion update(KindleFormattedBook formattedBook) {
        // Update a new version of an existing book in the Catalog
        String bookId = formattedBook.getBookId();
        // if there IS a bookID, it MAY BE an existing book,
        // We need to validate if it's in the catalog
        this.validateBookExists(bookId);

        // Use the existing removeBook method to set the current book version to be inactive
        this.removeBook(bookId);

        // Get this current book version so that you can increment the version
        int currentBookVersion = this.getLatestVersionOfBook(bookId).getVersion();

        CatalogItemVersion newVersion = new CatalogItemVersion();
        newVersion.setBookId(bookId);
        newVersion.setVersion(currentBookVersion+1);
        newVersion.setInactive(false);
        newVersion.setAuthor(formattedBook.getAuthor());
        newVersion.setGenre(formattedBook.getGenre());
        newVersion.setText(formattedBook.getText());
        newVersion.setTitle(formattedBook.getTitle());
        dynamoDbMapper.save(newVersion);

        return newVersion;
    }

}
