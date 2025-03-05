package com.pda.stockservice.dto.response;

import com.pda.stockservice.entity.Stock;
import com.pda.stockservice.entity.StockComment;
import com.pda.stockservice.feign.UserServiceClient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private int commentCnt;
    private List<CommentDTO> comments;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDTO{
        private Long commentId;
        private String nickname;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Short stockId;
        private String userId;
    }

    public static CommentResponseDTO toDTO(List<StockComment> comments, UserServiceClient userServiceClient) {
        List<CommentDTO> commentInfos = comments.stream()
                .map(comment -> {
                    NicknameResponseDTO nickname = new NicknameResponseDTO();
                    try {
                        nickname = userServiceClient.getNickname(comment.getUserId());
                        log.info("nickName = " + nickname);
                    } catch (Exception e) {
//                        nickname = "사용자" + comment.getUserId();
//                        System.out.println("Error fetching nickname for userId: " + comment.getUserId());
                        e.printStackTrace();
                    }

                    return CommentDTO.builder()
                            .commentId(comment.getCommentId())
                            .nickname(nickname.getNickname())
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .updatedAt(comment.getUpdatedAt())
                            .stockId(comment.getStock().getStockId())
                            .userId(comment.getUserId())
                            .build();
                })
                .collect(Collectors.toList());

        return CommentResponseDTO.builder()
                .commentCnt(commentInfos.size())
                .comments(commentInfos)
                .build();
    }



}
