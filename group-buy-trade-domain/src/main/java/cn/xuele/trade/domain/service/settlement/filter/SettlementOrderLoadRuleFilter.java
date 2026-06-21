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
 * 加载结算订单规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class SettlementOrderLoadRuleFilter implements ILogicHandler<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> {

    private final ITradeRepository tradeRepository;

    public SettlementOrderLoadRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeSettlementResultEntity apply(TradeSettlementCommandEntity requestParameter,
                                             TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity order = tradeRepository.queryOrderByUserIdAndOutTradeNo(
                requestParameter.getUserId(),
                requestParameter.getOutTradeNo());
        if (order == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单不存在");
        }
        if (!Objects.equals(order.getSource(), requestParameter.getSource())
                || !Objects.equals(order.getChannel(), requestParameter.getChannel())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单来源或渠道不一致");
        }
        dynamicContext.setOrder(order);
        return null;
    }
}
