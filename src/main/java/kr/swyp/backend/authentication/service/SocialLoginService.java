package kr.swyp.backend.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.member.enums.SocialLoginProviderType;

public interface SocialLoginService {

    MemberInfo getMemberInfoBySocialIdAndProviderType(String accessKey,
            SocialLoginProviderType providerType) throws JsonProcessingException;
}
