package kr.swyp.backend.authentication.dto;

import java.util.Map;

public abstract class Oauth2UserInfo {

    protected Map<String, Object> attributes;

    public Oauth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    // TODO: 차후에 Email을 받아오는 메소드를 추가해야 함.
    public abstract String getId();

    public abstract String getNickname();

    public abstract String getImageUrl();
}
