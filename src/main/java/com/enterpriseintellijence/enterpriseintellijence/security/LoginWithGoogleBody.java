package com.enterpriseintellijence.enterpriseintellijence.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginWithGoogleBody {
    String credential;
    String g_csrf_token;
}
