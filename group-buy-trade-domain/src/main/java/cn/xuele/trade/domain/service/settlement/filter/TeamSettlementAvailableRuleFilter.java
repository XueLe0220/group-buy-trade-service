package cn.xuele.trade.domain.service.settlement.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.service.settlement.factory.TradeSettlementRuleFilterFactory;

import java.util.Objects;

/**
 * 拼团队伍可结算规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class TeamSettlementAvailableRuleFilter implements ILogicHandler<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> {

    private final ITradeRepository tradeRepository;

    public TeamSettlementAvailableRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeSettlementResultEntity apply(TradeSettlementCommandEntity requestParameter,
                                             TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) {
        TradeOrderEntity order = dynamicContext.getOrder();
        GroupBuyTeamEntity team = tradeRepository.queryTeamByTeamId(order.getTeamId());
        if (team == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍不存在");
        }
        if (!Objects.equals(team.getActivityId(), order.getActivityId())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单与拼团队伍活动不一致");
        }
        if (!GroupBuyTeamStatusEnumVO.PROGRESS.equals(team.getStatus())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍状态不可结算");
        }
        if (team.getCompleteCount() == null || team.getTargetCount() == null || team.getCompleteCount() >= team.getTargetCount()) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍完成数已达目标，不能继续结算");
        }
        if (team.getValidEndTime() != null
                && requestParameter.getPayTime() != null
                && requestParameter.getPayTime().isAfter(team.getValidEndTime())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "支付时间超过拼团队伍有效期");
        }
        dynamicContext.setTeam(team);
        return null;
    }
}
