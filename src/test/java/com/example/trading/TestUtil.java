package com.example.trading;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TestUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }
}