package com.example.backend.post;

import com.example.backend.post.dto.CreatePostRequest;
import com.example.backend.post.dto.LikeToggleResponse;
import com.example.backend.post.dto.PopularPostResponse;
import com.example.backend.post.dto.PostResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody CreatePostRequest request,
                                               Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        Long authorId = Long.valueOf(authentication.getPrincipal().toString());
        Post post = postService.createPost(request.getTitle(), request.getContent(), authorId);
        String authorName = postService.getAuthorName(authorId);

        return ResponseEntity.ok(new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getAuthorId(),
            authorName,
            post.getCreatedAt(),
            post.getLikeCount(),
            false
        ));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getRecent(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(authentication.getPrincipal().toString());
        Page<Post> result = postService.getRecentPosts(page, size);
        List<PostResponse> response = result.stream()
            .map(post -> new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorId(),
                postService.getAuthorName(post.getAuthorId()),
                post.getCreatedAt(),
                post.getLikeCount(),
                postService.isLiked(post.getId(), userId)
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeToggleResponse> toggleLike(@PathVariable Long postId,
                                                         Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(authentication.getPrincipal().toString());
        return ResponseEntity.ok(postService.toggleLike(postId, userId));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularPostResponse>> getPopular(@RequestParam(defaultValue = "likes") String sort,
                                                                @RequestParam(defaultValue = "desc") String dir,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = Long.valueOf(authentication.getPrincipal().toString());
        Page<Post> result = postService.getPopularPosts(sort, dir, page, size, 20);
        List<PopularPostResponse> response = result.stream()
            .map(post -> new PopularPostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthorId(),
                postService.getAuthorName(post.getAuthorId()),
                post.getCreatedAt(),
                post.getLikeCount(),
                postService.isLiked(post.getId(), userId)
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
