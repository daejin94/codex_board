package com.example.backend.post.dto;

import java.time.Instant;

public class PopularPostResponse {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private Instant createdAt;
    private long likeCount;
    private boolean liked;

    public PopularPostResponse(Long id, String title, String content, Long authorId, String authorName, Instant createdAt, long likeCount, boolean liked) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.liked = liked;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public boolean isLiked() {
        return liked;
    }
}
