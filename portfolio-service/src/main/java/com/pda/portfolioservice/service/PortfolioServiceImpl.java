package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.MyPortfolioTitleResponseDTO;
import com.pda.portfolioservice.dto.response.ShareMyPortfolioResponseDTO;
import com.pda.portfolioservice.entity.MyPortfolio;
import com.pda.portfolioservice.entity.SharePortfolio;
import com.pda.portfolioservice.entity.SharePortfolioComment;
import com.pda.portfolioservice.repository.MyPortfolioRepository;
import com.pda.portfolioservice.repository.SharePortfolioCommentRepository;
import com.pda.portfolioservice.repository.SharePortfolioRepository;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.PortfolioHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final MyPortfolioRepository myPortfolioRepository;
    private final SharePortfolioRepository sharePortfolioRepository;
    private final SharePortfolioCommentRepository sharePortfolioCommentRepository;

    @Override
    @Transactional(readOnly = true)
    public MyPortfolioTitleResponseDTO.myPortfolioListDTO getMyPortfolioTitleList(Long myPortfolioId) {

        // 유저 ID를 임시로 1L로 설정
        String userId = "프디아";

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
    public void deleteMyPortfolio(Long myPortfolioId) {
        MyPortfolio myPortfolio = myPortfolioRepository.findById(myPortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        myPortfolioRepository.deleteById(myPortfolio.getMyPortfolioId());
    }

    @Override
    public void addComment(Long sharePortfolioId, SharePortfolioCommentRequestDTO requestDTO) {
        SharePortfolio sharePortfolio = sharePortfolioRepository.findById(sharePortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        SharePortfolioComment comment = SharePortfolioComment.builder()
                        .sharePortfolio(sharePortfolio)
                                .content(requestDTO.getContent())
                                        .build();

        sharePortfolioCommentRepository.save(comment);
    }
}
