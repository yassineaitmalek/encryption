package com.test.models.local.asymetric;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AsymetricType {

  @JsonProperty("RSA")
  RSA("RSA", "RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING", "encRSA", Boolean.FALSE),

  ;

  private final String algo;

  private final String transformation;

  private final String encExt;

  private final boolean requireInitializationVector;

}
