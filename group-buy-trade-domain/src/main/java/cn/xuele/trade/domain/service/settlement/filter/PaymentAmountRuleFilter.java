package cn.xuele.trade.domain.service.settlement.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.service.settlement.factory.TradeSettlementRuleFilterFactory;

/**
 * 支付金额校验规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class PaymentAmountRuleFilter implements ILogicHandler<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> {

    @Override
    public TradeSettlementResultEntity apply(TradeSettlementCommandEntity requestParameter,
                                             TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity order = dynamicContext.getOrder();
        if (order.getPayableAmount() == null || requestParameter.getPaidAmount() == null
                || order.getPayableAmount().compareTo(requestParameter.getPaidAmount()) != 0) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "支付金额与订单应付金额不一致");
        }
        return null;
    }
}
