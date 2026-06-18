package cn.xuele.trade.app.config;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.adapter.port.IActivityTrialPort;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.service.ITradeLockOrderService;
import cn.xuele.trade.domain.service.lock.TradeLockOrderService;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;
import cn.xuele.trade.domain.service.lock.filter.ActivityTrialRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.LockBuildRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.LockIdempotentRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.TeamAvailableRuleFilter;
import cn.xuele.trade.domain.service.lock.filter.UserTakeLimitRuleFilter;
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
            LockBuildRuleFilter lockBuildRuleFilter) {
        return tradeLockRuleFilterFactory.tradeLockRuleFilter(
                lockIdempotentRuleFilter,
                activityTrialRuleFilter,
                userTakeLimitRuleFilter,
                teamAvailableRuleFilter,
                lockBuildRuleFilter);
    }

    @Bean
    public ITradeLockOrderService tradeLockOrderService(
            ITradeRepository tradeRepository,
            BusinessLinkedList<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> tradeLockRuleFilter) {
        return new TradeLockOrderService(tradeRepository, tradeLockRuleFilter);
    }
}
