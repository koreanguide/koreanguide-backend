package com.koreanguide.koreanguidebackend.domain.review.controller;

import com.koreanguide.koreanguidebackend.config.security.JwtTokenProvider;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewCommentRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.data.dto.request.ReviewRequestDto;
import com.koreanguide.koreanguidebackend.domain.review.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"Review API"})
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

    @ApiOperation(value = "최근 리뷰 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentReview(HttpServletRequest request) {
        return reviewService.getRecentReview(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "사용자 모든 리뷰 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @GetMapping("/")
    public ResponseEntity<?> getAllReviewByUser(HttpServletRequest request) {
        return reviewService.getAllReview(jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")));
    }

    @ApiOperation(value = "트랙 ID로 리뷰 조회")
    @GetMapping("/get")
    public ResponseEntity<?> getReviewByTrackId(@RequestParam Long trackId) {
        return reviewService.getReview(trackId);
    }

    @ApiOperation(value = "트랙 리뷰 업로드")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    @PostMapping("/")
    public ResponseEntity<?> uploadReview(HttpServletRequest request, @RequestBody ReviewRequestDto reviewRequestDto) {
        return reviewService.uploadReview(
                jwtTokenProvider.getUserIdByToken(request.getHeader("X-AUTH-TOKEN")), reviewRequestDto);
    }

    @ApiOperation(value = "트랙 리뷰 대댓글 업로드")
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

    @ApiOperation(value = "리뷰 삭제")
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
