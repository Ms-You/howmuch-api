package com.dutchpay.howmuch.service;

import com.dutchpay.howmuch.common.exception.ErrorCode;
import com.dutchpay.howmuch.common.exception.GlobalException;
import com.dutchpay.howmuch.config.jwt.TokenDTO;
import com.dutchpay.howmuch.config.jwt.TokenProvider;
import com.dutchpay.howmuch.domain.Member;
import com.dutchpay.howmuch.domain.RoleType;
import com.dutchpay.howmuch.domain.dto.MemberDTO;
import com.dutchpay.howmuch.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    /**
     * 회원가입
     * @param signUpReq
     */
    public void register(MemberDTO.SignUpReq signUpReq) {
        checkEmailExists(signUpReq.getEmail());
        checkPasswordMatched(signUpReq.getPassword(), signUpReq.getPasswordConfirm());

        String birth = signUpReq.getBirthYear() + "-" + signUpReq.getBirthMonth() + "-" + signUpReq.getBirthDay();

        Member member = Member.builder()
                .email(signUpReq.getEmail())
                .password(passwordEncoder.encode(signUpReq.getPassword()))
                .name(signUpReq.getName())
                .birth(birth)
                .gender(signUpReq.getGender())
                .role(RoleType.ROLE_USER)
                .build();

        memberRepository.save(member);
    }

    private void checkEmailExists(String email) {
        if(memberRepository.findByEmail(email).isPresent()) {
            throw new GlobalException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkPasswordMatched(String password, String passwordConfirm) {
        if(!password.equals(passwordConfirm)) {
            throw new GlobalException(ErrorCode.PASSWORD_NOT_MATCHED);
        }
    }

    /**
     * 로그인
     * @param signInReq
     * @return TokenDTO
     */
    @Transactional
    public TokenDTO login(MemberDTO.SignInReq signInReq) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInReq.getEmail(), signInReq.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenDTO tokenDTO = tokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                authentication.getName(),
                tokenDTO.getRefreshToken(),
                tokenProvider.getExpiration(tokenDTO.getRefreshToken()),
                TimeUnit.MILLISECONDS
        );

        return tokenDTO;
    }
}
