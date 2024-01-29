package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;
import com.amazonaws.services.lambda.runtime.Context;

import javax.inject.Inject;

public class GetPublishingStatusActivity {
    @Inject
    public GetPublishingStatusActivity() {}

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        return null;
    }
}
