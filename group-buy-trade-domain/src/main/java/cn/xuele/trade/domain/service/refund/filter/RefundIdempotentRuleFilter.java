package cn.xuele.trade.domain.service.refund.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.domain.service.refund.factory.TradeRefundRuleFilterFactory;

/**
 * 重复退单幂等规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class RefundIdempotentRuleFilter implements ILogicHandler<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> {

    @Override
    public TradeRefundResultEntity apply(TradeRefundCommandEntity requestParameter,
                                         TradeRefundRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity order = dynamicContext.getOrder();
        if (!TradeOrderStatusEnumVO.CLOSE.equals(order.getStatus())) {
            return null;
        }
        return TradeRefundResultEntity.builder()
                .order(order)
                .team(dynamicContext.getTeam())
                .idempotentHit(true)
                .build();
    }
}
