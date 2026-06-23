package cn.xuele.trade.infrastructure.cache;


/**
 * 交易服务 Redis 缓存 key
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/23 10:50
 */
public class TradeCacheKey {
    private static final String TEAM_STOCK_RESERVED_KEY = "group-buy-trade:team-stock:reserved:";
    private static final String TEAM_STOCK_RECOVERED_KEY = "group-buy-trade:team-stock:recovered:";
    private static final String TEAM_STOCK_RECOVER_BIZ_KEY = "group-buy-trade:team-stock:recover-biz:";

    public static String getTeamStockRecoverBizKey(String teamId, String recoveryBizId) {
        return TEAM_STOCK_RECOVER_BIZ_KEY + teamId + ":" + recoveryBizId;
    }

    public static String getTeamStockReservedKey(String teamId) {
        return TEAM_STOCK_RESERVED_KEY + teamId;
    }

    public static String getTeamStockRecoveredKey(String teamId) {
        return TEAM_STOCK_RECOVERED_KEY + teamId;
    }
}
