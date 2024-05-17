package com.test.models.local.symetric;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SymetricType {

  @JsonProperty("AES")
  AES("AES", "AES/ECB/PKCS5Padding", "encAES", Boolean.FALSE),

  @JsonProperty("Blowfish")
  Blowfish("Blowfish", "Blowfish/CBC/PKCS5Padding", "encBlowfish", Boolean.FALSE),

  @JsonProperty("DES")
  DES("DES", "DES/CBC/PKCS5Padding", "encDES", Boolean.TRUE),

  @JsonProperty("DESede")
  DESede("DESede", "DESede/ECB/PKCS5Padding", "encDESede", Boolean.FALSE),

  @JsonProperty("RC2")
  RC2("RC2", "RC2/CBC/PKCS5Padding", "encRC2", Boolean.TRUE),

  @JsonProperty("RC4")
  RC4("RC4", "RC4", "encRC4", Boolean.FALSE);

  private final String algo;

  private final String transformation;

  private final String encExt;

  private final boolean requireInitializationVector;

}
