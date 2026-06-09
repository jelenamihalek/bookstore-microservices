package com.bookstore.service_library.decoder;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class DecoderTest {

    private final Decoder decoder = new Decoder();

    @Test
    void shouldDecodeHeaderCorrectly() {

        String credentials = "test@mail.com:password";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        String header = "Basic " + encoded;

        String result = decoder.decodeHeader(header);

        assertEquals("test@mail.com", result);
    }

    @Test
    void shouldThrow_whenHeaderInvalid() {

        String header = "InvalidHeader";

        assertThrows(Exception.class, () ->
                decoder.decodeHeader(header)
        );
    }


    @Test
    void shouldThrow_whenHeaderEmpty() {

        assertThrows(Exception.class, () ->
                decoder.decodeHeader("")
        );
    }
}