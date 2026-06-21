package cn.xuele.trade.domain.service.refund.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.service.refund.factory.TradeRefundRuleFilterFactory;

import java.util.Objects;

/**
 * 加载退单订单和队伍规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class RefundOrderLoadRuleFilter implements ILogicHandler<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> {

    private final ITradeRepository tradeRepository;

    public RefundOrderLoadRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeRefundResultEntity apply(TradeRefundCommandEntity requestParameter,
                                         TradeRefundRuleFilterFactory.DynamicContext dynamicContext) {
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

        GroupBuyTeamEntity team = tradeRepository.queryTeamByTeamId(order.getTeamId());
        if (team == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍不存在");
        }
        if (!Objects.equals(team.getActivityId(), order.getActivityId())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单与拼团队伍活动不一致");
        }

        dynamicContext.setOrder(order);
        dynamicContext.setTeam(team);
        return null;
    }
}
