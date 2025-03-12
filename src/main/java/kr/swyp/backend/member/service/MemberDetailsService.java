package kr.swyp.backend.member.service;

import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.dto.MemberDetails;
import kr.swyp.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ID 또는 비밀번호가 일치하지 않습니다."));
        return new MemberDetails(member.getMemberId(), member.getUsername(), member.getPassword(),
                member.getAuthorities());
    }
}
