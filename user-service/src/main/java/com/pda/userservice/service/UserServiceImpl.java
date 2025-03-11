package com.pda.userservice.service;

import com.pda.userservice.dto.request.JoinDTO;
import com.pda.userservice.dto.request.ProfileRequestDTO;
import com.pda.userservice.dto.response.*;
import com.pda.userservice.entity.Refresh;
import com.pda.userservice.entity.User;
import com.pda.userservice.feign.PortfolioServiceClient;
import com.pda.userservice.feign.StockServiceClient;
import com.pda.userservice.repository.UserRepository;
import com.pda.userservice.repository.RefreshRepository;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.UserHandler;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;
    private final StockServiceClient stockServiceClient;
    private final PortfolioServiceClient portfolioServiceClient;

    @Transactional
    @Override
    public void updateTelegramChatId(String token, String chatId) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        User user = userRepository.findByUserId(userId);
        System.out.println("User found: " + user.getUsername());
        System.out.println(chatId);
        user.setTelegramChatId(chatId);
        System.out.println("User found: " + user.getTelegramChatId());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public String getTelegramChatId(String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        User user = userRepository.findByUserId(userId);

        return user.getTelegramChatId();
    }

    @Transactional
    @Override
    public void deleteTelegramChatId(String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        User user = userRepository.findByUserId(userId);

        user.setTelegramChatId(null);  // ID 삭제
        userRepository.save(user);
    }

    @Override
    public ApiResponse<StocksResponseDTO> stocks(String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        List<MyStockWatchlistResponseDTO> myStockWatchlist = stockServiceClient.getMyStockWatchlist(userId);

        // CommentsResponseDTO 생성
        StocksResponseDTO commentsResponseDTO = StocksResponseDTO.builder()
                .stockCnt(myStockWatchlist.size())
                .stockInfos(myStockWatchlist)
                .build();

        return ApiResponse.onSuccess(commentsResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public String getTelegramChatIdUserId(String userId) {
        User user = userRepository.findByUserId(userId);

        if (user == null || user.getTelegramChatId() == null) {
            throw new UserHandler(ErrorStatus.USER_NOT_FOUND);
        }

        return user.getTelegramChatId();
    }

    @Override
    public ApiResponse<Void> join(JoinDTO joinDTO) {
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 아이디 중복 확인
        if (userRepository.existsByUsername(username)) {
            return ApiResponse.onSuccess(HttpStatus.CONFLICT.value(), "중복된 아이디 입니다.");
        }

        // 사용자 정보 생성 및 저장
        User user = joinDTO.toUserEntity(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);  // DB에 저장
        return ApiResponse.onSuccess(HttpStatus.OK.value(), "회원가입 성공");
    }

    @Override
    public ApiResponse<Void> handleReissue(HttpServletRequest request, HttpServletResponse response) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));

//        String refresh = extractRefreshToken(request);
        String refresh = request.getHeader("Refresh");


        // Refresh 토큰이 헤더에 있는지 확인
        if (refresh == null) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(토큰 없음)");
        }

        // Refresh 토큰 만료 여부 확인
        if (isTokenExpired(refresh)) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(토큰 만료)");
        }

        // Refresh 토큰이 맞는지 확인
        if (!isRefreshTokenValid(refresh)) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(refresh 토큰이 아님");
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // DB에 토큰 존재 여부 확인
        if (!isTokenStoredInDB(refresh, username)) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "토큰 재발급 실패(DB에 없음)");
        }

        // userId 조회
        String userId = userRepository.findByUsername(username).getUserId();

        String newAccess = jwtUtil.createJwt("access", userId, username, role, 600000L); // 10분 유효기간
//        response.setHeader("Authorization", "Bearer " + newAccess);
        response.setHeader("access", newAccess);

        return ApiResponse.onSuccess(HttpStatus.OK.value(), "토큰 재발급 성공");
    }

    @Override
    public NicknameResponseDTO getNicknameByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        return NicknameResponseDTO.toDTO(user);
    }

    @Override
    public ApiResponse<Void> profile(ProfileRequestDTO profileRequestDTO, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUserId(userId));

        if (optionalUser.isEmpty()) {
            return ApiResponse.onFailure(HttpStatus.BAD_REQUEST.value(), "사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        // DTO의 toUpdatedUser() 메서드를 이용해 새로운 User 객체 생성
        User updatedUser = profileRequestDTO.toUserEntity(user);

        // 변경된 객체를 저장 (JPA save() 필요)
        userRepository.save(updatedUser);

        return ApiResponse.onSuccess(HttpStatus.OK.value(), "프로필 업데이트 성공");
    }

    @Override
    public ApiResponse<CommentsResponseDTO> comments(String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        MyStockCommentsResponseDTO myStockComments = stockServiceClient.getMyStockComments(userId);
        MyPortfolioCommentsResponseDTO myPortfolioComments = portfolioServiceClient.getMyPortfolioComments(userId);

//        // test
//        MyStockCommentsResponseDTO myStockComments = stockServiceClient.getMyStockComments("bd703313-cbc6-4aef-8363-e632efcc793a");
//        MyPortfolioCommentsResponseDTO myPortfolioComments = portfolioServiceClient.getMyPortfolioComments("bd703313-cbc6-4aef-8363-e632efcc793a");

        System.out.println(myStockComments.getCommentsS().size());

        // CommentsResponseDTO 생성
        CommentsResponseDTO commentsResponseDTO = CommentsResponseDTO.builder()
                .commentsS(myStockComments.getCommentsS()) // List<StockCommentResponseDTO>가 직접 들어가도록 수정
                .commentsP(myPortfolioComments.getCommentsP()) // List<PortfolioCommentResponseDTO>가 직접 들어가도록 수정
                .build();



        return ApiResponse.onSuccess(commentsResponseDTO);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isTokenExpired(String token) {
        try {
            JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
            jwtUtil.isExpired(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private boolean isRefreshTokenValid(String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        return "refresh".equals(jwtUtil.getCategory(token));
    }

    private boolean isTokenStoredInDB(String token, String username) {
        Optional<Refresh> byId = refreshRepository.findById(username);
        return byId.isPresent() && token.equals(byId.get().getRefresh());
    }
}
