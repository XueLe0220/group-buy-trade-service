package cn.xuele.trade.domain.model.valobj;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 交易事件类型。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/24
 */
@Getter
@RequiredArgsConstructor
public enum TradeEventTypeEnumVO {

    TEAM_COMPLETED("TEAM_COMPLETED", "GROUP_BUY_TEAM", "拼团队伍已成团");

    private final String code;
    private final String aggregateType;
    private final String info;
}
