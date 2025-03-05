package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.entity.SharePortfolioComment;
import com.pda.portfolioservice.feign.UserServiceClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class SharePortfolioCommentResponseDTO {
    private int commentsCnt;
    private List<CommentDTO> comments;


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDTO {
        private Long commentId;
        private String nickname;
        private String userId;
        private String content;
        private String date;
    }

    public static SharePortfolioCommentResponseDTO toDTO(List<SharePortfolioComment> comments, UserServiceClient userServiceClient) {
        List<CommentDTO> commentInfos = comments.stream()
                .map(comment -> {
                    NicknameResponseDTO nickname = new NicknameResponseDTO();
                    return CommentDTO.builder()
                            .commentId(comment.getCommentId())
                            .nickname(nickname.getNickname())
                            .content(comment.getContent())
                            .date(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .userId(comment.getUserId())
                            .build();
                })
                .collect(Collectors.toList());
        return SharePortfolioCommentResponseDTO.builder()
                .commentsCnt(commentInfos.size())
                .comments(commentInfos)
                .build();
    }
}
