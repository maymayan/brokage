package com.maymayan.brokage.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserJwtTokenModel {

    private String token;
    private String role;

}
