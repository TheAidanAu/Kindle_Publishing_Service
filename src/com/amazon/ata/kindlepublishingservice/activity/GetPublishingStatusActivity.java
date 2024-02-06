package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetPublishingStatusActivity {
    private PublishingStatusDao publishingStatusDao;
    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        String publishingRecordId = publishingStatusRequest.getPublishingRecordId();
        // implement a getPublishingStatus method in PublishingStatusDao
        List<PublishingStatusItem> publishingStatusItems =
                publishingStatusDao.getPublishingStatus(publishingRecordId);

        // This is a parameter to populate a GetPublishingStatusResponse object
        List<PublishingStatusRecord> publishingStatusHistory = new ArrayList<>();

        for (PublishingStatusItem status: publishingStatusItems) {
            PublishingStatusRecord record = new PublishingStatusRecord(
                    status.getStatus().toString(), status.getStatusMessage(),status.getBookId());
            publishingStatusHistory.add(record);
        }

        // Return a GetPublishingStatusResponse object. When populating the response,
        // you will need to convert the list of PublishingStatusItems to a list of PublishingStatusRecords.
        // Follow the pattern used in GetBookActivity when converting the list of BookRecommendations.
        // There's another constructor too
        return GetPublishingStatusResponse.builder().
                withPublishingStatusHistory(publishingStatusHistory).build();
    }
}
