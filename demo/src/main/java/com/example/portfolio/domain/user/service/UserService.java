package com.example.portfolio.domain.user.service;

import com.example.portfolio.domain.user.dto.*;
import com.example.portfolio.domain.user.entity.EmailVerificationToken;
import com.example.portfolio.domain.user.entity.User;
import com.example.portfolio.domain.user.repository.EmailVerificationTokenRepository;
import com.example.portfolio.domain.user.repository.UserRepository;
import com.example.portfolio.global.exception.*;
import com.example.portfolio.global.util.FileUtil;
import com.example.portfolio.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailAsyncSender mailAsyncSender;

    public boolean isUsernameTaken(String username) { return userRepository.existsByUsername(username); }
    public boolean isEmailTaken(String email)       { return userRepository.existsByEmail(email); }
    public boolean isNicknameTaken(String nickname) { return userRepository.existsByNickname(nickname); }

    @Transactional
    public void sendVerificationCode(String email) {
        if (userRepository.existsByEmail(email)) throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        tokenRepository.deleteAllByEmail(email);
        EmailVerificationToken token = tokenRepository.save(EmailVerificationToken.create(email));
        mailAsyncSender.sendVerificationCode(email, token.getCode());
    }

    @Transactional
    public void verifyCode(String email, String code) {
        EmailVerificationToken token = tokenRepository.findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_TOKEN_INVALID));
        if (token.isExpired()) throw new CustomException(ErrorCode.EMAIL_TOKEN_EXPIRED);
        if (!token.matches(code)) throw new CustomException(ErrorCode.EMAIL_CODE_MISMATCH);
        token.verify();
    }

    @Transactional
    public void signup(SignupRequest request) {
        if (!request.isPasswordMatch())         throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        validatePassword(request.getPassword());
        if (userRepository.existsByUsername(request.getUsername())) throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        if (userRepository.existsByEmail(request.getEmail()))       throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        if (request.getNickname() != null && !request.getNickname().isBlank()
                && userRepository.existsByNickname(request.getNickname())) throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        if (!tokenRepository.existsByEmailAndVerifiedTrue(request.getEmail())) throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        try {
            userRepository.save(User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .email(request.getEmail())
                    .nickname(request.getNickname())
                    .build());
            tokenRepository.deleteAllByEmail(request.getEmail());
            mailAsyncSender.sendWelcome(request.getEmail(), request.getNickname());
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    // 비밀번호 찾기 - 임시 비밀번호 발급
    @Transactional
    public void sendPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 임시 비밀번호 8자리 생성
        String tempPassword = generateTempPassword();
        user.changePassword(passwordEncoder.encode(tempPassword));
        mailAsyncSender.sendPasswordReset(email, tempPassword);
    }

    public UserResponse getUser(Long id) { return new UserResponse(findUser(id)); }

    @Transactional
    public void updateProfile(Long userId, UserUpdateRequest request, MultipartFile profileImage) throws IOException {
        User user = findUser(userId);
        String imagePath = (profileImage != null && !profileImage.isEmpty()) ? FileUtil.saveFile(profileImage) : null;
        user.updateProfile(request.getNickname(), request.getBio(), imagePath);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = findUser(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        validatePassword(request.getNewPassword());
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit  = password.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) throw new CustomException(ErrorCode.PASSWORD_TOO_WEAK);
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < 10; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
