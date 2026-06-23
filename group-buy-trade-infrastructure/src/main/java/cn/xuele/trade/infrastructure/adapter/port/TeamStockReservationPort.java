package cn.xuele.trade.infrastructure.adapter.port;

import cn.xuele.trade.domain.adapter.port.ITeamStockReservationPort;
import cn.xuele.trade.infrastructure.cache.TradeCacheKey;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 队伍库存预占 Redis
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/23 11:25
 */
@Component
@RequiredArgsConstructor
public class TeamStockReservationPort implements ITeamStockReservationPort {

    private static final String TEAM_STOCK_RESERVE_SCRIPT = loadScript("redis/script/team_stock_reserve.lua");
    private static final String TEAM_STOCK_RECOVER_SCRIPT = loadScript("redis/script/team_stock_recover.lua");

    private final RedissonClient redissonClient;

    @Override
    public boolean reserve(String teamId, int targetCount, int currentLockCount, LocalDateTime validEndTime) {
        String reservedKey = TradeCacheKey.getTeamStockReservedKey(teamId);
        String recoveredKey = TradeCacheKey.getTeamStockRecoveredKey(teamId);

        Duration ttl = Duration.between(LocalDateTime.now(), validEndTime).plusHours(1);
        if (!ttl.isPositive()) return false;

        Long result = redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE,
                TEAM_STOCK_RESERVE_SCRIPT,
                RScript.ReturnType.INTEGER,
                List.of(reservedKey, recoveredKey),
                targetCount,
                currentLockCount,
                ttl.toMillis()
        );

        return Long.valueOf(1L).equals(result);
    }

    @Override
    public void recover(String teamId, String recoveryBizId, LocalDateTime validEndTime) {
        if (teamId == null || recoveryBizId == null || validEndTime == null) return;

        Duration ttl = Duration.between(LocalDateTime.now(), validEndTime).plusHours(1);
        if (!ttl.isPositive()) return;

        String recoveredKey = TradeCacheKey.getTeamStockRecoveredKey(teamId);
        String recoverBizKey = TradeCacheKey.getTeamStockRecoverBizKey(teamId, recoveryBizId);

        redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE,
                TEAM_STOCK_RECOVER_SCRIPT,
                RScript.ReturnType.INTEGER,
                List.of(recoveredKey, recoverBizKey),
                ttl.toMillis()
        );
    }

    private static String loadScript(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("load redis lua script failed: " + path, e);
        }
    }
}
