package com.example.backend.post;

import com.example.backend.post.dto.LikeToggleResponse;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Post createPost(String title, String content, Long authorId) {
        Post post = new Post(title, content, authorId, Instant.now());
        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<Post> getRecentPosts(int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Transactional(readOnly = true)
    public String getAuthorName(Long authorId) {
        User user = userRepository.findById(authorId).orElse(null);
        return user == null ? "알 수 없음" : user.getEmail();
    }

    @Transactional
    public LikeToggleResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }

        boolean exists = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        if (exists) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            postLikeRepository.save(new PostLike(postId, userId, Instant.now()));
            post.setLikeCount(post.getLikeCount() + 1);
        }

        postRepository.save(post);
        return new LikeToggleResponse(postId, !exists, post.getLikeCount());
    }

    @Transactional(readOnly = true)
    public Page<Post> getPopularPosts(String sort, String dir, int page, int size, long minLikes) {
        String sortValue = (sort == null || sort.isBlank()) ? "likes" : sort;
        String dirValue = (dir == null || dir.isBlank()) ? "desc" : dir;
        String sortField = "likes".equalsIgnoreCase(sortValue) ? "likeCount" : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(dirValue) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        return postRepository.findByLikeCountGreaterThanEqual(minLikes, pageable);
    }
}
