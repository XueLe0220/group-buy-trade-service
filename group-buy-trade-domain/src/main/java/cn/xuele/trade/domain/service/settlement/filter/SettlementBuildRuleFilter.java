package cn.xuele.trade.domain.service.settlement.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.trade.domain.model.aggregate.GroupBuySettlementAggregate;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.service.settlement.factory.TradeSettlementRuleFilterFactory;

/**
 * 结算聚合构建规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class SettlementBuildRuleFilter implements ILogicHandler<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> {

    @Override
    public TradeSettlementResultEntity apply(TradeSettlementCommandEntity requestParameter,
                                             TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        dynamicContext.setSettlementAggregate(GroupBuySettlementAggregate.builder()
                .command(requestParameter)
                .order(dynamicContext.getOrder())
                .team(dynamicContext.getTeam())
                .build());
        return null;
    }
}
