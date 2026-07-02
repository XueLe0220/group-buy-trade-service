package cn.xuele.trade.app.job;

import cn.xuele.trade.infrastructure.event.TradeEventRelayResult;
import cn.xuele.trade.infrastructure.event.TradeEventRelayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 交易事件 outbox relay 定时任务。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Component
public class TradeEventRelayJob {

    private static final Logger log = LoggerFactory.getLogger(TradeEventRelayJob.class);

    private final TradeEventRelayService relayService;

    public TradeEventRelayJob(TradeEventRelayService relayService) {
        this.relayService = relayService;
    }

    @Scheduled(fixedDelayString = "${trade.event.relay.fixed-delay:5000}")
    public void exec() {
        TradeEventRelayResult result = relayService.relayOnce();
        if (result.getTotal() > 0) {
            log.info("trade event relay done, total={}, success={}, retry={}, fail={}",
                    result.getTotal(), result.getSuccess(), result.getRetry(), result.getFail());
        }
    }
}
