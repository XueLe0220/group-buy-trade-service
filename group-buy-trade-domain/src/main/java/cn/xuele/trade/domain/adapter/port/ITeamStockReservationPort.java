package cn.xuele.trade.domain.adapter.port;

import java.time.LocalDateTime;

/**
 * 库存占用防腐端口
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/23 10:48
 */
public interface ITeamStockReservationPort {

    boolean reserve(String teamId, int targetCount, int currentLockCount, LocalDateTime validEndTime);

    void recover(String teamId, String recoveryBizId, LocalDateTime validEndTime);

}
