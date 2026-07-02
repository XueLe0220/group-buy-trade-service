package cn.xuele.trade.app.config;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.adapter.port.IActivityTrialPort;
import cn.xuele.trade.domain.adapter.port.ITeamStockReservationPort;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.service.ITradeLockOrderService;
import cn.xuele.trade.domain.service.ITradePayOrderService;
import cn.xuele.trade.domain.service.ITradeRefundOrderService;
import cn.xuele.trade.domain.service.ITradeSettlementOrderService;
import cn.xuele.trade.domain.service.lock.TradeLockOrderService;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;
import cn.xuele.trade.domain.service.lock.filter.ActivityTrialRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.LockBuildRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.LockIdempotentRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.TeamAvailableRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.TeamStockReserveRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.UserTakeLimitRuleFilter;
import cn.xuele.trade.domain.service.pay.TradePayOrderService;
import cn.xuele.trade.domain.service.refund.TradeRefundOrderService;
import cn.xuele.trade.domain.service.refund.factory.TradeRefundRuleFilterFactory;
import cn.xuele.trade.domain.service.refund.filter.RefundBuildRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundIdempotentRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundOrderLoadRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundTypeRuleFilter;
import cn.xuele.trade.domain.service.settlement.TradeSettlementOrderService;
import cn.xuele.trade.domain.service.settlement.factory.TradeSettlementRuleFilterFactory;
import cn.xuele.trade.domain.service.settlement.filter.PaymentAmountRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.PaymentNoRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.SettlementBuildRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.SettlementOrderLoadRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.SettlementOrderStatusRuleFilter;
import cn.xuele.trade.domain.service.settlement.filter.TeamSettlementAvailableRuleFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public LockIdempotentRuleFilter lockIdempotentRuleFilter(ITradeRepository tradeRepository) {
        return new LockIdempotentRuleFilter(tradeRepository);
    }

    @Bean
    public ActivityTrialRuleFilter activityTrialRuleFilter(IActivityTrialPort activityTrialPort) {
        return new ActivityTrialRuleFilter(activityTrialPort);
    }

    @Bean
    public UserTakeLimitRuleFilter userTakeLimitRuleFilter(ITradeRepository tradeRepository) {
        return new UserTakeLimitRuleFilter(tradeRepository);
    }

    @Bean
    public TeamAvailableRuleFilter teamAvailableRuleFilter(ITradeRepository tradeRepository) {
        return new TeamAvailableRuleFilter(tradeRepository);
    }

    @Bean
    public TeamStockReserveRuleFilter teamStockReserveRuleFilter(ITeamStockReservationPort teamStockReservationPort) {
        return new TeamStockReserveRuleFilter(teamStockReservationPort);
    }

    @Bean
    public LockBuildRuleFilter lockBuildRuleFilter() {
        return new LockBuildRuleFilter();
    }

    @Bean
    public TradeLockRuleFilterFactory tradeLockRuleFilterFactory() {
        return new TradeLockRuleFilterFactory();
    }

    @Bean
    public BusinessLinkedList<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> tradeLockRuleFilter(
            TradeLockRuleFilterFactory tradeLockRuleFilterFactory,
            LockIdempotentRuleFilter lockIdempotentRuleFilter,
            ActivityTrialRuleFilter activityTrialRuleFilter,
            UserTakeLimitRuleFilter userTakeLimitRuleFilter,
            TeamAvailableRuleFilter teamAvailableRuleFilter,
            TeamStockReserveRuleFilter teamStockReserveRuleFilter,
            LockBuildRuleFilter lockBuildRuleFilter) {
        return tradeLockRuleFilterFactory.tradeLockRuleFilter(
                lockIdempotentRuleFilter,
                activityTrialRuleFilter,
                userTakeLimitRuleFilter,
                teamAvailableRuleFilter,
                teamStockReserveRuleFilter,
                lockBuildRuleFilter);
    }

    @Bean
    public ITradeLockOrderService tradeLockOrderService(
            ITradeRepository tradeRepository,
            ITeamStockReservationPort teamStockReservationPort,
            BusinessLinkedList<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> tradeLockRuleFilter) {
        return new TradeLockOrderService(tradeRepository, teamStockReservationPort, tradeLockRuleFilter);
    }

    @Bean
    public ITradePayOrderService tradePayOrderService(ITradeRepository tradeRepository) {
        return new TradePayOrderService(tradeRepository);
    }

    @Bean
    public SettlementOrderLoadRuleFilter settlementOrderLoadRuleFilter(ITradeRepository tradeRepository) {
        return new SettlementOrderLoadRuleFilter(tradeRepository);
    }

    @Bean
    public SettlementOrderStatusRuleFilter settlementOrderStatusRuleFilter(ITradeRepository tradeRepository) {
        return new SettlementOrderStatusRuleFilter(tradeRepository);
    }

    @Bean
    public PaymentAmountRuleFilter paymentAmountRuleFilter() {
        return new PaymentAmountRuleFilter();
    }

    @Bean
    public PaymentNoRuleFilter paymentNoRuleFilter(ITradeRepository tradeRepository) {
        return new PaymentNoRuleFilter(tradeRepository);
    }

    @Bean
    public TeamSettlementAvailableRuleFilter teamSettlementAvailableRuleFilter(ITradeRepository tradeRepository) {
        return new TeamSettlementAvailableRuleFilter(tradeRepository);
    }

    @Bean
    public SettlementBuildRuleFilter settlementBuildRuleFilter() {
        return new SettlementBuildRuleFilter();
    }

    @Bean
    public TradeSettlementRuleFilterFactory tradeSettlementRuleFilterFactory() {
        return new TradeSettlementRuleFilterFactory();
    }

    @Bean
    public BusinessLinkedList<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> tradeSettlementRuleFilter(
            TradeSettlementRuleFilterFactory tradeSettlementRuleFilterFactory,
            SettlementOrderLoadRuleFilter settlementOrderLoadRuleFilter,
            SettlementOrderStatusRuleFilter settlementOrderStatusRuleFilter,
            PaymentAmountRuleFilter paymentAmountRuleFilter,
            PaymentNoRuleFilter paymentNoRuleFilter,
            TeamSettlementAvailableRuleFilter teamSettlementAvailableRuleFilter,
            SettlementBuildRuleFilter settlementBuildRuleFilter) {
        return tradeSettlementRuleFilterFactory.tradeSettlementRuleFilter(
                settlementOrderLoadRuleFilter,
                settlementOrderStatusRuleFilter,
                paymentAmountRuleFilter,
                paymentNoRuleFilter,
                teamSettlementAvailableRuleFilter,
                settlementBuildRuleFilter);
    }

    @Bean
    public ITradeSettlementOrderService tradeSettlementOrderService(
            ITradeRepository tradeRepository,
            BusinessLinkedList<TradeSettlementCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementResultEntity> tradeSettlementRuleFilter) {
        return new TradeSettlementOrderService(tradeRepository, tradeSettlementRuleFilter);
    }

    @Bean
    public RefundOrderLoadRuleFilter refundOrderLoadRuleFilter(ITradeRepository tradeRepository) {
        return new RefundOrderLoadRuleFilter(tradeRepository);
    }

    @Bean
    public RefundIdempotentRuleFilter refundIdempotentRuleFilter() {
        return new RefundIdempotentRuleFilter();
    }

    @Bean
    public RefundTypeRuleFilter refundTypeRuleFilter() {
        return new RefundTypeRuleFilter();
    }

    @Bean
    public RefundBuildRuleFilter refundBuildRuleFilter() {
        return new RefundBuildRuleFilter();
    }

    @Bean
    public TradeRefundRuleFilterFactory tradeRefundRuleFilterFactory() {
        return new TradeRefundRuleFilterFactory();
    }

    @Bean
    public BusinessLinkedList<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> tradeRefundRuleFilter(
            TradeRefundRuleFilterFactory tradeRefundRuleFilterFactory,
            RefundOrderLoadRuleFilter refundOrderLoadRuleFilter,
            RefundIdempotentRuleFilter refundIdempotentRuleFilter,
            RefundTypeRuleFilter refundTypeRuleFilter,
            RefundBuildRuleFilter refundBuildRuleFilter) {
        return tradeRefundRuleFilterFactory.tradeRefundRuleFilter(
                refundOrderLoadRuleFilter,
                refundIdempotentRuleFilter,
                refundTypeRuleFilter,
                refundBuildRuleFilter);
    }

    @Bean
    public ITradeRefundOrderService tradeRefundOrderService(
            ITradeRepository tradeRepository,
            ITeamStockReservationPort teamStockReservationPort,
            BusinessLinkedList<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext, TradeRefundResultEntity> tradeRefundRuleFilter) {
        return new TradeRefundOrderService(tradeRepository, teamStockReservationPort, tradeRefundRuleFilter);
    }
}
