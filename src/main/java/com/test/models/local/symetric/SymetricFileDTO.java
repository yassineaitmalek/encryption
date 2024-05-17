package com.test.models.local.symetric;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SymetricFileDTO {

  private MultipartFile file;

  private String secretKey;

  private SymetricType symetricType;

}
