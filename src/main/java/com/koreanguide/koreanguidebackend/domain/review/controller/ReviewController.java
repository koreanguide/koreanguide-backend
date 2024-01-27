package com.koreanguide.koreanguidebackend.domain.review.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewCommentRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.service.ReviewService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ReviewController(ReviewService reviewService, JwtTokenProvider jwtTokenProvider) {
        this.reviewService = reviewService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentReview(HttpServletRequest request) {
        return reviewService.getRecentReview(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/")
    public ResponseEntity<?> getAllReviewByUser(HttpServletRequest request) {
        return reviewService.getAllReview(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getReviewByTrackId(@RequestParam Long trackId) {
        return reviewService.getReview(trackId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/")
    public ResponseEntity<?> uploadReview(HttpServletRequest request, @RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.uploadReview(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")), reviewRequestDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/comment")
    public ResponseEntity<?> uploadReviewComment(HttpServletRequest request,
                                                 @RequestBody ReviewCommentRequestDto reviewCommentRequestDto) {
        return reviewService.uploadReviewComment(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")), reviewCommentRequestDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @DeleteMapping("/")
    public ResponseEntity<?> deleteReview(HttpServletRequest request, @RequestParam Long reviewId) {
        return reviewService.deleteReview(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")), reviewId
        );
    }
}
