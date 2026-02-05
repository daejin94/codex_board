package com.example.backend.data;

import com.example.backend.post.Post;
import com.example.backend.post.PostRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserGrant;
import com.example.backend.user.UserGrantRepository;
import com.example.backend.user.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository,
                                UserGrantRepository grantRepository,
                                PostRepository postRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("Seed start: users={}, posts={}", userRepository.count(), postRepository.count());
            User user = userRepository.findByEmail("user@example.com").orElse(null);
            if (user == null) {
                UserGrant grant = new UserGrant("ROLE_USER");
                grant = grantRepository.save(grant);

                Instant now = Instant.now();
                user = new User(
                    "user@example.com",
                    passwordEncoder.encode("password123"),
                    "010-1234-5678",
                    now,
                    now,
                    null,
                    false,
                    grant
                );
                user = userRepository.save(user);
                log.info("Seed user created: id={}", user.getId());
            } else {
                log.info("Seed user exists: id={}", user.getId());
            }

            long postCount = postRepository.count();
            if (postCount == 0) {
                Instant now = Instant.now();
                for (int i = 1; i <= 20; i++) {
                    String title = "오늘의 기록 " + i;
                    String content = "이것은 한글로 작성된 샘플 게시글 내용입니다. " + i + "번째 게시글입니다.";
                    Instant createdAt = now.minus(i, ChronoUnit.HOURS);
                    Post post = new Post(title, content, user.getId(), createdAt);
                    if (i <= 6) {
                        post.setLikeCount(30);
                    }
                    postRepository.save(post);
                }
                log.info("Seed posts created: count=20");
            } else {
                log.info("Seed posts skipped: existing={}", postCount);
            }
        };
    }
}