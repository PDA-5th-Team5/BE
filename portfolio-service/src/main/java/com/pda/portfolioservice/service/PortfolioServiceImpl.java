package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.request.StockFilterRequest;
import com.pda.portfolioservice.dto.response.*;
import com.pda.portfolioservice.entity.MyPortfolio;
import com.pda.portfolioservice.entity.SharePortfolio;
import com.pda.portfolioservice.entity.SharePortfolioComment;
import com.pda.portfolioservice.feign.StockServiceClient;
import com.pda.portfolioservice.feign.UserServiceClient;
import com.pda.portfolioservice.model.Portfolio;
import com.pda.portfolioservice.repository.MyPortfolioRepository;
import com.pda.portfolioservice.repository.PortfolioRepository;
import com.pda.portfolioservice.repository.SharePortfolioCommentRepository;
import com.pda.portfolioservice.repository.SharePortfolioRepository;
import com.pda.utilservice.response.ApiResponse;
import com.pda.utilservice.jwt.JWTUtil;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.PortfolioHandler;
import com.pda.utilservice.response.exception.handler.StockHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final MyPortfolioRepository myPortfolioRepository;
    private final SharePortfolioRepository sharePortfolioRepository;
    private final SharePortfolioCommentRepository sharePortfolioCommentRepository;
    private final PortfolioRepository portfolioRepository;
    private final StockServiceClient stockServiceClient;

    private final UserServiceClient userServiceClient;
    private final Environment environment;

    // 포트폴리오 저장 (중복 검사 후 저장)
    @Override
    public Portfolio saveMyPortfolio(Portfolio portfolio, String userId) {
        // 1. MySQL에 먼저 저장 (ID 자동 생성)
        MyPortfolio myPortfolio = MyPortfolio.builder()
                .title(portfolio.getTitle())
                .description(portfolio.getDescription())
                .userId(userId)
                .build();
        myPortfolio = myPortfolioRepository.save(myPortfolio); // 저장 후 ID 생성됨
        Long generatedPortfolioId = myPortfolio.getMyPortfolioId(); // 생성된 ID 가져오기

        // 2. MongoDB 저장할 때 portfolioId 세팅
        portfolio.setPortfolioId(generatedPortfolioId);
        portfolio.setCategory("my"); // 기본값 설정 (필요 시 변경 가능)

        // 3. 기존에 동일한 portfolioId와 category가 존재하는지 확인
        Optional<Portfolio> existingPortfolio = portfolioRepository.findByCategoryAndPortfolioId(
                portfolio.getCategory(), portfolio.getPortfolioId());

        if (existingPortfolio.isPresent()) {
            System.out.println("중복된 포트폴리오가 이미 존재합니다: " + portfolio.getPortfolioId());
            throw new IllegalStateException("이미 존재하는 포트폴리오입니다.");
        }

        // 4. MongoDB에 저장
        return portfolioRepository.save(portfolio);
    }

    // 특정 포트폴리오 조회
    @Override
    public Portfolio getPortfolio(String category, Long portfolioId) {
        return portfolioRepository.findByCategoryAndPortfolioId(category, portfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));
    }

    //포트폴리오 종목 리스트 조회
    @Override
    public List<StockResponseDTO> getPortfolioStock(Portfolio portfolio, int page) {

        StockFilterRequest stockFilterRequest = new StockFilterRequest();
        stockFilterRequest.setFilters(portfolio.toStockFilter());
        stockFilterRequest.setMarketType(portfolio.getMarket());
        stockFilterRequest.setSector(portfolio.getSector());
        ApiResponse<List<StockResponseDTO>> stocks = stockServiceClient.searchStockStatIds(stockFilterRequest,page);

        return stocks.getData();
    }

    //특정 포트폴리오 삭제
    @Override
    public void deletePortfolio(String category, Long portfolioId) {
        Portfolio portfolio = getPortfolio(category, portfolioId);
        portfolioRepository.delete(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public MyPortfolioTitleResponseDTO.myPortfolioListDTO getMyPortfolioTitleList(Long myPortfolioId , String userId) {

        // 유저 ID를 임시로 1L로 설정

        MyPortfolio myPortfolio = myPortfolioRepository.findById(myPortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        List<MyPortfolio> myPortfolioList = myPortfolioRepository.findByUserId(userId);

        return MyPortfolioTitleResponseDTO.myPortfolioListDTO.toDTO(myPortfolioList);


    }


    @Override
    public ShareMyPortfolioResponseDTO shareMyPortfolio(Long myPortfolioId) {
        MyPortfolio myPortfolio = myPortfolioRepository.findById(myPortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        SharePortfolio sharePortfolio = new SharePortfolio();
        sharePortfolio.setTitle(myPortfolio.getTitle());
        sharePortfolio.setDescription(myPortfolio.getDescription());
        sharePortfolio.setUserId(myPortfolio.getUserId());
        sharePortfolio.setCreatedAt(LocalDateTime.now());
        sharePortfolio.setLoadCount(0);

        SharePortfolio savedSharePortfolio = sharePortfolioRepository.save(sharePortfolio);
        return new ShareMyPortfolioResponseDTO(savedSharePortfolio.getSharePortfolioId());
    }

    @Override
    public void deleteMyPortfolio( Long myPortfolioId) {
        MyPortfolio myPortfolio = myPortfolioRepository.findById(myPortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        myPortfolioRepository.deleteById(myPortfolio.getMyPortfolioId());
    }

    @Override
    public void addComment(Long sharePortfolioId, SharePortfolioCommentRequestDTO requestDTO, String token) {
        System.out.println("token = " + token);
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        System.out.println(userId);

        SharePortfolio sharePortfolio = sharePortfolioRepository.findById(sharePortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        SharePortfolioComment comment = SharePortfolioComment.builder()
                        .sharePortfolio(sharePortfolio)
                        .userId(userId)
                        .content(requestDTO.getContent())
                        .build();

        sharePortfolioCommentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public SharePortfolioCommentResponseDTO getComments(Long sharePortfolioId) {
        if (!sharePortfolioRepository.existsById(sharePortfolioId)) {
            throw new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND);
        }

        List<SharePortfolioComment> comments = sharePortfolioCommentRepository.findBysharePortfolio_SharePortfolioId(sharePortfolioId);

        return SharePortfolioCommentResponseDTO.toDTO(comments, userServiceClient);
    }

    @Override
    public void updateComment(Long sharePortfolioId, Long commentId, SharePortfolioCommentRequestDTO requestDTO, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        SharePortfolioComment comment = sharePortfolioCommentRepository.findById(commentId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_COMMENT_NOT_FOUND));

        if (!comment.getSharePortfolio().getSharePortfolioId().equals(sharePortfolioId)) {
            throw new PortfolioHandler(ErrorStatus.PORTFOLIO_COMMENT_NOT_INCLUDED);
        }

        if (!comment.getUserId().equals(userId)) {
            throw new PortfolioHandler(ErrorStatus.UNAUTHORIZED);
        }

        comment.setContent(requestDTO.getContent());
        sharePortfolioCommentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long sharePortfolioId, Long commentId, String token) {
        JWTUtil jwtUtil = new JWTUtil(Objects.requireNonNull(environment.getProperty("spring.jwt.secret")));
        String userId = jwtUtil.getBearerUserId(token);

        SharePortfolioComment comment = sharePortfolioCommentRepository.findById(commentId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_COMMENT_NOT_FOUND));

        if (!comment.getUserId().equals(userId)) {
            throw new PortfolioHandler(ErrorStatus.UNAUTHORIZED);
        }

        sharePortfolioCommentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public MyCommentsResponseDTO getCommentsByUserId(String userId) {
        List<SharePortfolioComment> comments = sharePortfolioCommentRepository.findByUserId(userId)
                .orElseThrow(() -> new StockHandler(ErrorStatus.MY_COMMENTS_NOT_FOUND));
        return MyCommentsResponseDTO.toDTO(comments);
    }


}
