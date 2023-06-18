package com.enterpriseintellijence.enterpriseintellijence.security.rateLimiter;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import com.enterpriseintellijence.enterpriseintellijence.exception.ManyRequestException;
import com.enterpriseintellijence.enterpriseintellijence.security.JwtContextUtils;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {


    private final UserTypePlanService userTypePlanService;
    private final JwtContextUtils jwtContextUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userTypeKey;
        User userLogged = jwtContextUtils.getUserLoggedFromContext();
        if(userLogged!= null){
            if(userLogged.isAdministrator()) {
                userTypeKey = "ADMIN-" + jwtContextUtils.getUserLoggedFromContext().getUsername();
            } else {
                userTypeKey = "BASIC-" + jwtContextUtils.getUserLoggedFromContext().getUsername();
            }
        }else {
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            userTypeKey = "BASIC-" + ipAddress;
        }


        Bucket tokenBucket = userTypePlanService.resolveBucket(userTypeKey);
        ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            throw new ManyRequestException("You have exhausted your API Request Quota");
        }
    }
}
