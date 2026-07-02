package cn.xuele.trade.infrastructure.event;

/**
 * 交易事件 relay 执行结果。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
public class TradeEventRelayResult {

    private final int total;
    private final int success;
    private final int retry;
    private final int fail;

    public TradeEventRelayResult(int total, int success, int retry, int fail) {
        this.total = total;
        this.success = success;
        this.retry = retry;
        this.fail = fail;
    }

    public int getTotal() {
        return total;
    }

    public int getSuccess() {
        return success;
    }

    public int getRetry() {
        return retry;
    }

    public int getFail() {
        return fail;
    }
}
