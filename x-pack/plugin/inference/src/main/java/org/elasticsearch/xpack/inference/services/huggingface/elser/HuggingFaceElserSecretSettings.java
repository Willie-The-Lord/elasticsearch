/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.inference.services.huggingface.elser;

import org.elasticsearch.TransportVersion;
import org.elasticsearch.TransportVersions;
import org.elasticsearch.common.ValidationException;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.settings.SecureString;
import org.elasticsearch.inference.ModelSecrets;
import org.elasticsearch.inference.SecretSettings;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xpack.inference.services.MapParsingUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public record HuggingFaceElserSecretSettings(SecureString apiKey) implements SecretSettings {
    public static final String NAME = "hugging_face_elser_secret_settings";

    static final String API_KEY = "api_key";

    public static HuggingFaceElserSecretSettings fromMap(Map<String, Object> map) {
        ValidationException validationException = new ValidationException();

        String apiToken = MapParsingUtils.removeAsType(map, API_KEY, String.class);

        if (apiToken == null) {
            validationException.addValidationError(MapParsingUtils.missingSettingErrorMsg(API_KEY, ModelSecrets.SECRET_SETTINGS));
        } else if (apiToken.isEmpty()) {
            validationException.addValidationError(MapParsingUtils.mustBeNonEmptyString(API_KEY, ModelSecrets.SECRET_SETTINGS));
        }

        if (validationException.validationErrors().isEmpty() == false) {
            throw validationException;
        }

        SecureString secureApiToken = new SecureString(Objects.requireNonNull(apiToken).toCharArray());

        return new HuggingFaceElserSecretSettings(secureApiToken);
    }

    public HuggingFaceElserSecretSettings {
        Objects.requireNonNull(apiKey);
    }

    public HuggingFaceElserSecretSettings(StreamInput in) throws IOException {
        this(in.readSecureString());
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(API_KEY, apiKey.toString());
        builder.endObject();
        return builder;
    }

    @Override
    public TransportVersion getMinimalSupportedVersion() {
        return TransportVersions.ML_INFERENCE_TASK_SETTINGS_OPTIONAL_ADDED;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeSecureString(apiKey);
    }
}
