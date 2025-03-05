package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.entity.SharePortfolioComment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MyPortfolioCommentsResponseDTO {
    private List<PortfolioCommentResponseDTO> commentsP;

    // StockComment 리스트를 받아 MyCommentsResponseDTO 변환 메서드
    public static MyPortfolioCommentsResponseDTO toDTO(List<SharePortfolioComment> comments) {
        return MyPortfolioCommentsResponseDTO.builder()
                .commentsP(comments.stream()
                        .map(PortfolioCommentResponseDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}

@Getter
@Builder
class PortfolioCommentResponseDTO {
    private Long connectId;  // stock_id
    private String name;     // 종목명
    private Long commentId;
    private String content;
    private String date;

    // StockComment 엔티티를 받아 DTO 변환 메서드
    public static PortfolioCommentResponseDTO fromEntity(SharePortfolioComment comment) {
        return PortfolioCommentResponseDTO.builder()
                .connectId(comment.getSharePortfolio().getSharePortfolioId())  // share_portfolio_id 가져오기
                .name(comment.getSharePortfolio().getTitle())  // 포트폴리오명 가져오기
                .commentId(comment.getCommentId())  // 댓글 ID
                .content(comment.getContent())  // 댓글 내용
                .date(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))  // 날짜 포맷 변환
                .build();
    }
}