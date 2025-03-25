package kr.yuns.seoul.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.enums.SeoulCountry;
import kr.yuns.seoul.data.entity.SeoulRiverPark;
import kr.yuns.seoul.data.enums.RiverPark;

public interface SeoulRiverParkRepository extends JpaRepository<SeoulRiverPark, Long> {
    List<SeoulRiverPark> getAllByParkCountry(SeoulCountry seoulCountry);
    SeoulRiverPark getByParkEn(RiverPark riverPark);
}