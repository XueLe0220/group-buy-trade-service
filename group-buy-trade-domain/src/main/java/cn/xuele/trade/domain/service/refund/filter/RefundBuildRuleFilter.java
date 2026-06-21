package cn.xuele.trade.domain.service.refund.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.trade.domain.model.aggregate.GroupBuyRefundAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import cn.xuele.trade.domain.service.refund.factory.TradeRefundRuleFilterFactory;

/**
 * 退单聚合构建规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class RefundBuildRuleFilter implements ILogicHandler<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> {

    @Override
    public TradeRefundResultEntity apply(TradeRefundCommandEntity requestParameter,
                                         TradeRefundRuleFilterFactory.DynamicContext dynamicContext) {
        RefundTypeEnumVO refundType = dynamicContext.getRefundType();
        GroupBuyTeamStatusEnumVO targetTeamStatus = resolveTargetTeamStatus(refundType, dynamicContext.getTeam());
        dynamicContext.setRefundAggregate(GroupBuyRefundAggregate.builder()
                .command(requestParameter)
                .order(dynamicContext.getOrder())
                .team(dynamicContext.getTeam())
                .refundType(refundType)
                .targetTeamStatus(targetTeamStatus)
                .build());
        return null;
    }

    private GroupBuyTeamStatusEnumVO resolveTargetTeamStatus(RefundTypeEnumVO refundType, GroupBuyTeamEntity team) {
        if (!RefundTypeEnumVO.PAID_FORMED.equals(refundType)) {
            return team.getStatus();
        }
        Integer completeCount = team.getCompleteCount();
        if (completeCount == null || completeCount <= 1) {
            return GroupBuyTeamStatusEnumVO.FAIL;
        }
        return GroupBuyTeamStatusEnumVO.PARTIAL_REFUND;
    }
}
