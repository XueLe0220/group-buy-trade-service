package cn.xuele.trade.infrastructure.dao;

import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易事件 outbox DAO。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Mapper
public interface ITradeEventOutboxDao {

    int insert(TradeEventOutbox event);

    List<TradeEventOutbox> queryPendingEvents(@Param("limit") int limit);

    int markSent(@Param("eventId") String eventId, @Param("sentTime") LocalDateTime sentTime);

    int markRetry(@Param("eventId") String eventId,
                  @Param("retryCount") int retryCount,
                  @Param("nextRetryTime") LocalDateTime nextRetryTime,
                  @Param("lastError") String lastError);

    int markFailed(@Param("eventId") String eventId,
                   @Param("retryCount") int retryCount,
                   @Param("lastError") String lastError);
}
