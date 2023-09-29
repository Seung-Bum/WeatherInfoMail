package com.items.Util;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class Base64Config {
	
    public static byte[] base64Encoding(String text) {
        
        byte[] targetBytes = text.getBytes();
        
        // Base64 인코딩
        Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetBytes);
        return encodedBytes;
    }
    
    public static String base64Decoding(byte[] encodingText) {
           
        // Base64 디코딩
        Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodingText);
        
        String decodingText = new String(decodedBytes);
        return decodingText;
    }
}
