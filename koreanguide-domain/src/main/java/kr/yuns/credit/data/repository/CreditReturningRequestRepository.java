package kr.yuns.credit.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.entity.User;
import kr.yuns.credit.data.entity.CreditReturningRequest;

import java.util.List;

public interface CreditReturningRequestRepository extends JpaRepository<CreditReturningRequest, Long> {
    List<CreditReturningRequest> getAllByUser(User user);
}