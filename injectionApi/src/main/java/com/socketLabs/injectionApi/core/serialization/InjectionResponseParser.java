package com.socketLabs.injectionApi.core.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socketLabs.injectionApi.*;
import okhttp3.Response;

import java.io.IOException;

/**
 * Used by the HttpClient to convert the response form the Injection API.
 */
public class InjectionResponseParser {

    /**
     * Parse the response from theInjection Api into SendResponse
     * @param response The response from the Injection Api request
     * @return A SendResponse from the Injection Api response
     * @throws IOException in case of a network error.
     */
    public SendResponse Parse(Response response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        InjectionResponseDto injectionResponse = mapper.readValue(response.body().string(), InjectionResponseDto.class);

        SendResult resultEnum = DetermineSendResult(injectionResponse, response.networkResponse().code());
        SendResponse newResponse = new SendResponse(resultEnum);
        newResponse.setTransactionReceipt(injectionResponse.getTransactionReceipt());

        if (resultEnum == SendResult.Warning && (injectionResponse.getMessageResults() != null && injectionResponse.getMessageResults().size() > 0))
        {
            SendResult r = SendResult.fromString(injectionResponse.getMessageResults().get(0).getErrorCode());
            newResponse.setResult(r);
        }

        if (injectionResponse.getMessageResults() != null && injectionResponse.getMessageResults().size() > 0)
            newResponse.setAddressResults(injectionResponse.getMessageResults().get(0).getAddressResults());

        return newResponse;

    }

    /**
     * Enumerated SendResult of the payload response from the Injection Api
     * @param responseDto The InjectionResponseDto from the Injection Api
     * @param responseCode The HTTP response code from the Injection Api
     * @return The SendResult from the Injection Api response
     */
    private SendResult DetermineSendResult(InjectionResponseDto responseDto, int responseCode) {

        switch (responseCode) {

            case 200: //HttpStatusCode.OK
                SendResult r = SendResult.fromString(responseDto.getErrorCode());
                if (r == null)
                    return SendResult.UnknownError;
                return r;

            case 500: //HttpStatusCode.InternalServerError
                return SendResult.InternalError;

            case 408: //HttpStatusCode.RequestTimeout
                return SendResult.Timeout;

            case 401: //HttpStatusCode.Unauthorized
                return SendResult.InvalidAuthentication;

            default:
                return SendResult.UnknownError;
        }
    }

}
