package kr.yuns.seoul.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.yuns.seoul.data.entity.DustData;
import kr.yuns.seoul.data.entity.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveWeatherData(String key, WeatherData weatherData) throws JsonProcessingException {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = new ObjectMapper().writeValueAsString(weatherData);
        ops.set(key, value, 10, TimeUnit.HOURS);
    }

    public Optional<WeatherData> getWeatherData(String key) throws JsonProcessingException {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get(key);
        if (value != null) {
            WeatherData weatherData = new ObjectMapper().readValue(value, WeatherData.class);
            return Optional.of(weatherData);
        }
        return Optional.empty();
    }

    public void saveDustData(String key, DustData dustData) throws JsonProcessingException {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = new ObjectMapper().writeValueAsString(dustData);
        ops.set(key, value, 1, TimeUnit.HOURS);
    }

    public Optional<DustData> getDustData(String key) throws JsonProcessingException {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get(key);
        if (value != null) {
            DustData dustData = new ObjectMapper().readValue(value, DustData.class);
            return Optional.of(dustData);
        }
        return Optional.empty();
    }
}