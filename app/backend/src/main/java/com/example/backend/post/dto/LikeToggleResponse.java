package com.example.backend.post.dto;

public class LikeToggleResponse {

    private Long postId;
    private boolean liked;
    private long likeCount;

    public LikeToggleResponse(Long postId, boolean liked, long likeCount) {
        this.postId = postId;
        this.liked = liked;
        this.likeCount = likeCount;
    }

    public Long getPostId() {
        return postId;
    }

    public boolean isLiked() {
        return liked;
    }

    public long getLikeCount() {
        return likeCount;
    }
}
