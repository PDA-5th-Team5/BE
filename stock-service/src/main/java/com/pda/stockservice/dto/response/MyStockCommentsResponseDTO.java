package com.pda.stockservice.dto.response;

import com.pda.stockservice.entity.StockComment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MyStockCommentsResponseDTO {
    private List<StockCommentResponseDTO> commentsS;

    // StockComment 리스트를 받아 MyCommentsResponseDTO 변환 메서드
    public static MyStockCommentsResponseDTO toDTO(List<StockComment> comments) {
        return MyStockCommentsResponseDTO.builder()
                .commentsS(comments.stream()
                        .map(StockCommentResponseDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}

@Getter
@Builder
class StockCommentResponseDTO {
    private Short connectId;  // stock_id
    private String name;     // 종목명
    private Long commentId;
    private String content;
    private String date;

    // StockComment 엔티티를 받아 DTO 변환 메서드
    public static StockCommentResponseDTO fromEntity(StockComment comment) {
        return StockCommentResponseDTO.builder()
                .connectId(comment.getStock().getStockId())  // stock_id 가져오기
                .name(comment.getStock().getCompanyName())  // 종목명 가져오기
                .commentId(comment.getCommentId())  // 댓글 ID
                .content(comment.getContent())  // 댓글 내용
                .date(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))  // 날짜 포맷 변환
                .build();
    }
}