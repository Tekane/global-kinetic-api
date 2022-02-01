package com.example.globalkineticapi.event.listener;

import com.example.globalkineticapi.cache.LoggedOutJwtTokenCache;
import com.example.globalkineticapi.event.OnUserLogoutSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OnUserLogoutSuccessEventListener implements ApplicationListener<OnUserLogoutSuccessEvent> {

    private final LoggedOutJwtTokenCache tokenCache;
    private static final Logger logger = LoggerFactory.getLogger(OnUserLogoutSuccessEventListener.class);

    @Autowired
    public OnUserLogoutSuccessEventListener(LoggedOutJwtTokenCache tokenCache) {
        this.tokenCache = tokenCache;
    }

    @Override
    public void onApplicationEvent(OnUserLogoutSuccessEvent event) {
        if (null != event) {
            logger.info(String.format("Log out success event received for user [%s]", event.getUsername()));
            tokenCache.markLogoutEventForToken(event);
        }
    }
}
