package com.pda.portfolioservice.service;

import com.pda.portfolioservice.dto.request.SharePortfolioCommentRequestDTO;
import com.pda.portfolioservice.dto.response.*;
import com.pda.portfolioservice.entity.MyPortfolio;
import com.pda.portfolioservice.entity.SharePortfolio;
import com.pda.portfolioservice.entity.SharePortfolioComment;
import com.pda.portfolioservice.feign.StockServiceClient;
import com.pda.portfolioservice.feign.UserServiceClient;
import com.pda.portfolioservice.repository.MyPortfolioRepository;
import com.pda.portfolioservice.repository.SharePortfolioCommentRepository;
import com.pda.portfolioservice.repository.SharePortfolioRepository;
import com.pda.utilservice.response.code.resultCode.ErrorStatus;
import com.pda.utilservice.response.exception.handler.PortfolioHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final StockServiceClient stockServiceClient;
    private final UserServiceClient userServiceClient;
    private final MyPortfolioRepository myPortfolioRepository;
    private final SharePortfolioRepository sharePortfolioRepository;
    private final SharePortfolioCommentRepository sharePortfolioCommentRepository;


    @Override
    @Transactional(readOnly = true)
    public PortfolioSummaryResponseDTO getPortfolioSummary(Long myPortfolioId) {
        MyPortfolio myPortfolio = myPortfolioRepository.findById(myPortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        return PortfolioSummaryResponseDTO.toDTO(myPortfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public MyPortfolioTitleResponseDTO.myPortfolioListDTO getMyPortfolioTitleList() {

        // 유저 ID를 임시로 1L로 설정
        String userId = "1L";

//        MyPortfolio myPortfolio = myPortfolioRepository.findById()
//                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        List<MyPortfolio> myPortfolioList = myPortfolioRepository.findAll();

        return MyPortfolioTitleResponseDTO.myPortfolioListDTO.toDTO(myPortfolioList);


    }


    @Override
    public ShareMyPortfolioResponseDTO shareMyPortfolio(Long myPortfolioId) {
        MyPortfolio myPortfolio = myPortfolioRepository.findById(myPortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        SharePortfolio sharePortfolio = SharePortfolio.builder()
                .title(myPortfolio.getTitle())
                .description(myPortfolio.getDescription())
                .userId(myPortfolio.getUserId())
                .createdAt(LocalDateTime.now())
                .loadCount(0)
                .build();

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
    @Transactional(readOnly = true)
    public SharePortfolioListResponseDTO getSharePortfolios(String sort, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<SharePortfolio> sharePortfolios = sharePortfolioRepository.findAll(pageRequest);

        List<SharePortfolioListResponseDTO.SharePortfolioDTO> sharePortfolioDTOList = sharePortfolios.stream()
                .map(sharePortfolio -> SharePortfolioListResponseDTO.SharePortfolioDTO.builder()
                        .sharePortfolioId(sharePortfolio.getSharePortfolioId())
                        .sharePortfolioTitle(sharePortfolio.getTitle())
                        .sharePortfolioDescription(sharePortfolio.getDescription())
                        .sharePortfolioImportCnt(sharePortfolio.getLoadCount())
                        .build()
                ).collect(Collectors.toList());

        return SharePortfolioListResponseDTO.builder()
                .sharePortfoliosCnt(sharePortfolioDTOList.size())
                .sharePortfolios(sharePortfolioDTOList)
                .build();
    }

    @Override
    public ImportSharePortfolioResponseDTO getSharePortfolio(Long sharePortfolioId) {
        SharePortfolio sharePortfolio = sharePortfolioRepository.findById(sharePortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        MyPortfolio myPortfolio = MyPortfolio.builder()
                .title(sharePortfolio.getTitle())
                .description(sharePortfolio.getDescription())
                .userId(sharePortfolio.getUserId())
                .createdAt(LocalDateTime.now())
                .build();

        MyPortfolio savedMyPortfolio = myPortfolioRepository.save(myPortfolio);
        return new ImportSharePortfolioResponseDTO(savedMyPortfolio.getMyPortfolioId());
    }

    @Override
    @Transactional(readOnly = true)
    public SharePortfolioSummaryResponseDTO getSharePortfolioSummary(Long sharePortfolioId) {
        SharePortfolio sharePortfolio = sharePortfolioRepository.findById(sharePortfolioId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_NOT_FOUND));

        return SharePortfolioSummaryResponseDTO.toDTO(sharePortfolio);
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

    @Override
    @Transactional(readOnly = true)
    public SharePortfolioCommentResponseDTO getComments(Long sharePortfolioId) {
        List<SharePortfolioComment> comments = sharePortfolioCommentRepository.findAll();

        if (comments.isEmpty()) {
            return SharePortfolioCommentResponseDTO.builder()
                    .commentsCnt(0)
                    .comments(List.of())
                    .build();
        }

        return SharePortfolioCommentResponseDTO.toDTO(comments);

    }

    @Override
    public void updateComment(Long sharePortfolioId, Long commentId, SharePortfolioCommentRequestDTO requestDTO) {
        SharePortfolioComment comment = sharePortfolioCommentRepository.findById(commentId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_COMMENT_NOT_FOUND));

        if (!comment.getSharePortfolio().getSharePortfolioId().equals(sharePortfolioId)) {
            throw new PortfolioHandler(ErrorStatus.PORTFOLIO_COMMENT_NOT_INCLUDED);
        }

        comment.setContent(requestDTO.getContent());
        sharePortfolioCommentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long sharePortfolioId, Long commentId) {
        SharePortfolioComment comment = sharePortfolioCommentRepository.findById(commentId)
                .orElseThrow(() -> new PortfolioHandler(ErrorStatus.PORTFOLIO_COMMENT_NOT_FOUND));

        sharePortfolioCommentRepository.deleteById(commentId);

    }


}
