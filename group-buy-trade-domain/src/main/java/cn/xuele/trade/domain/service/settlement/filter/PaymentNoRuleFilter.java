package cn.xuele.trade.domain.service.settlement.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.service.settlement.factory.TradeSettlementRuleFilterFactory;

import java.util.Objects;

/**
 * 支付流水号防串单规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class PaymentNoRuleFilter implements ILogicHandler<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> {

    private final ITradeRepository tradeRepository;

    public PaymentNoRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeSettlementResultEntity apply(TradeSettlementCommandEntity requestParameter,
                                             TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity order = dynamicContext.getOrder();
        TradeOrderEntity payNoOrder = tradeRepository.queryOrderByPayNo(requestParameter.getPayNo());
        if (payNoOrder != null && !Objects.equals(payNoOrder.getOrderId(), order.getOrderId())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "支付流水号已绑定其他交易订单");
        }
        dynamicContext.setPayNoOrder(payNoOrder);
        return null;
    }
}
