package com.test.models.local.asymetric;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsymetricEncryptionFileDTO {

  private MultipartFile file;

  private String publicKey;

  private AsymetricType asymetricType;

}
