package cn.xuele.trade.domain.service.refund.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import cn.xuele.trade.domain.service.refund.factory.TradeRefundRuleFilterFactory;

/**
 * 退单状态组合识别规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class RefundTypeRuleFilter implements ILogicHandler<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> {

    @Override
    public TradeRefundResultEntity apply(TradeRefundCommandEntity requestParameter,
                                         TradeRefundRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity order = dynamicContext.getOrder();
        GroupBuyTeamEntity team = dynamicContext.getTeam();
        dynamicContext.setRefundType(RefundTypeEnumVO.getRefundType(team.getStatus(), order.getStatus()));
        return null;
    }
}
