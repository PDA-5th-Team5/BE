package com.pda.portfolioservice.dto.response;

import com.pda.portfolioservice.entity.SharePortfolioComment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class SharePortfolioCommentResponseDTO {
    private int commentsCnt;
    private List<CommentDTO> comments;

    public static SharePortfolioCommentResponseDTO toDTO(List<SharePortfolioComment> comments) {
        List<CommentDTO> details = comments.stream()
                .map(CommentDTO::toDTO)
                .collect(Collectors.toList());

        return SharePortfolioCommentResponseDTO.builder()
                .commentsCnt(details.size())
                .comments(details)
                .build();
    }

    @Builder
    @Getter
    public static class CommentDTO {
        private Long commentId;
        private String nickname;
        private Long userId;
        private String content;
        private String date;

        public static CommentDTO toDTO(SharePortfolioComment comment) {
            return CommentDTO.builder()
                    .commentId(comment.getCommentId())
//                    .nickname(comment.getContent())
                    .date(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();
        }
    }
}
