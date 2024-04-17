package com.koreanguide.koreanguidebackend.domain.seoul.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.enums.SeoulCountry;
import com.koreanguide.koreanguidebackend.domain.seoul.data.entity.SeoulRiverPark;
import com.koreanguide.koreanguidebackend.domain.seoul.data.enums.RiverPark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeoulRiverParkRepository extends JpaRepository<SeoulRiverPark, Long> {
    List<SeoulRiverPark> getAllByParkCountry(SeoulCountry seoulCountry);
    SeoulRiverPark getByParkEn(RiverPark riverPark);
}
