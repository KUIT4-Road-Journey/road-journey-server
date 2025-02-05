package com.road_journey.road_journey.auth.dao;

import com.road_journey.road_journey.auth.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findMemberByAccountId(String accountId);
}
