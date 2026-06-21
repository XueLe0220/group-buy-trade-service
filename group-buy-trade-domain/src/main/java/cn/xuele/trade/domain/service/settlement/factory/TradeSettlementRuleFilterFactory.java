package cn.xuele.trade.domain.service.settlement.factory;

import cn.xuele.common.design.framework.link.LinkArmory;
import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.model.aggregate.GroupBuySettlementAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.service.settlement.filter.PaymentAmountRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.PaymentNoRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.SettlementBuildRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.SettlementOrderLoadRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.SettlementOrderStatusRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.TeamSettlementAvailableRuleFilter;
import lombok.Data;

/**
 * 交易结算规则链工厂。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class TradeSettlementRuleFilterFactory {

    public BusinessLinkedList<TradeSettlementCommandEntity, DynamicContext, TradeSettlementResultEntity> tradeSettlementRuleFilter(
            SettlementOrderLoadRuleFilter settlementOrderLoadRuleFilter,
            SettlementOrderStatusRuleFilter settlementOrderStatusRuleFilter,
            PaymentAmountRuleFilter paymentAmountRuleFilter,
            PaymentNoRuleFilter paymentNoRuleFilter,
            TeamSettlementAvailableRuleFilter teamSettlementAvailableRuleFilter,
            SettlementBuildRuleFilter settlementBuildRuleFilter) {

        LinkArmory<TradeSettlementCommandEntity, DynamicContext, TradeSettlementResultEntity> linkArmory =
                new LinkArmory<>("交易结算规则链",
                        settlementOrderLoadRuleFilter,
                        settlementOrderStatusRuleFilter,
                        paymentAmountRuleFilter,
                        paymentNoRuleFilter,
                        teamSettlementAvailableRuleFilter,
                        settlementBuildRuleFilter);

        return linkArmory.getLogicLink();
    }

    @Data
    public static class DynamicContext {

        /** 支付回调对应的个人订单。 */
        private TradeOrderEntity order;

        /** 支付流水号已绑定的订单。 */
        private TradeOrderEntity payNoOrder;

        /** 订单所属拼团队伍。 */
        private GroupBuyTeamEntity team;

        /** 最终进入仓储事务的结算聚合。 */
        private GroupBuySettlementAggregate settlementAggregate;

    }
}
