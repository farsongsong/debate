package com.example.portfolio.domain.vote.service;

import com.example.portfolio.domain.post.repository.PostRepository;
import com.example.portfolio.domain.user.repository.UserRepository;
import com.example.portfolio.domain.vote.entity.*;
import com.example.portfolio.domain.vote.repository.VoteRepository;
import com.example.portfolio.global.exception.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class VoteService {
    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public VoteResult getVoteResult(Long postId) {
        long agree = voteRepository.countByPostIdAndVoteType(postId, VoteType.AGREE);
        long disagree = voteRepository.countByPostIdAndVoteType(postId, VoteType.DISAGREE);
        long total = agree + disagree;
        int agreePercent = total == 0 ? 50 : (int) Math.round((double) agree / total * 100);
        return new VoteResult(agree, disagree, total, agreePercent, 100 - agreePercent);
    }

    public String getMyVote(Long postId, Long userId) {
        return voteRepository.findByPostIdAndUserId(postId, userId)
                .map(v -> v.getVoteType().name()).orElse(null);
    }

    @Transactional
    public VoteResult vote(Long postId, Long userId, String voteTypeStr) {
        VoteType voteType = VoteType.valueOf(voteTypeStr);
        var post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        var user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        voteRepository.findByPostIdAndUserId(postId, userId)
                .ifPresentOrElse(
                    v -> v.changeVote(voteType),
                    () -> voteRepository.save(Vote.builder().post(post).user(user).voteType(voteType).build())
                );
        return getVoteResult(postId);
    }

    @Getter
    public static class VoteResult {
        private final long agree, disagree, total;
        private final int agreePercent, disagreePercent;
        public VoteResult(long agree, long disagree, long total, int agreePercent, int disagreePercent) {
            this.agree = agree; this.disagree = disagree; this.total = total;
            this.agreePercent = agreePercent; this.disagreePercent = disagreePercent;
        }
    }
}
