package com.campus.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimRequest {

    @NotBlank(message = "认领说明不能为空")
    private String proof;
}
