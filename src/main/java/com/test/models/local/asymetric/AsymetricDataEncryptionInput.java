package com.test.models.local.asymetric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsymetricDataEncryptionInput {

  private String data;

  private String publicKey;

  private AsymetricType asymetricType;

}
