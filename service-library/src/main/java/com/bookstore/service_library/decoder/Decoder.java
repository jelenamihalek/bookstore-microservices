package com.bookstore.service_library.decoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class Decoder {
	
	public String decodeHeader(String authorization) {

		String base64Credentials = authorization.substring("Basic".length()).trim();

		byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);

		String credentials = new String(credDecoded, StandardCharsets.UTF_8);

		String[] values = credentials.split(":", 2);

		return values[0];

	}

}