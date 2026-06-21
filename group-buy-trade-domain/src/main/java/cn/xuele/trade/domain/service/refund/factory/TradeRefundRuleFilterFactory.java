package cn.xuele.trade.domain.service.refund.factory;

import cn.xuele.common.design.framework.link.LinkArmory;
import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.model.aggregate.GroupBuyRefundAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import cn.xuele.trade.domain.service.refund.filter.RefundBuildRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundIdempotentRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundOrderLoadRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundTypeRuleFilter;
import lombok.Data;

/**
 * 交易退单规则链工厂。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class TradeRefundRuleFilterFactory {

    public BusinessLinkedList<TradeRefundCommandEntity, DynamicContext, TradeRefundResultEntity> tradeRefundRuleFilter(
            RefundOrderLoadRuleFilter refundOrderLoadRuleFilter,
            RefundIdempotentRuleFilter refundIdempotentRuleFilter,
            RefundTypeRuleFilter refundTypeRuleFilter,
            RefundBuildRuleFilter refundBuildRuleFilter) {

        LinkArmory<TradeRefundCommandEntity, DynamicContext, TradeRefundResultEntity> linkArmory =
                new LinkArmory<>("交易退单规则链",
                        refundOrderLoadRuleFilter,
                        refundIdempotentRuleFilter,
                        refundTypeRuleFilter,
                        refundBuildRuleFilter);

        return linkArmory.getLogicLink();
    }

    @Data
    public static class DynamicContext {

        /** 退单对应的个人订单。 */
        private TradeOrderEntity order;

        /** 订单所属拼团队伍。 */
        private GroupBuyTeamEntity team;

        /** 状态组合识别出的退单类型。 */
        private RefundTypeEnumVO refundType;

        /** 最终进入仓储事务的退单聚合。 */
        private GroupBuyRefundAggregate refundAggregate;

    }
}
