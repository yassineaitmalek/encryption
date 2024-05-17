package com.test.utility;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EncryptionUtility {

  public static byte[] stringToBytes(String str) {

    return Base64.getDecoder().decode(str.getBytes());
  }

  public static byte[] stringToBytesISO(String str) {

    return str.getBytes(StandardCharsets.ISO_8859_1);
  }

  public static String bytesToString(byte[] bytes) {
    return new String(Base64.getEncoder().encode(bytes));
  }

}
