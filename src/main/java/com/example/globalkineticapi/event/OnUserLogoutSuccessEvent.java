package com.example.globalkineticapi.event;

import com.example.globalkineticapi.dto.LogOutRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
@Getter
@Setter
public class OnUserLogoutSuccessEvent extends ApplicationEvent  {

    private static final long serialVersionUID = 1L;
    private final String username;
    private final String token;
    private final Date eventTime;
    private final transient LogOutRequest logOutRequest;

    public OnUserLogoutSuccessEvent(String username, String token,  LogOutRequest logOutRequest) {
        super(username);
        this.username = username;
        this.token = token;
        this.logOutRequest = logOutRequest;
        this.eventTime = Date.from(Instant.now());
    }
}
