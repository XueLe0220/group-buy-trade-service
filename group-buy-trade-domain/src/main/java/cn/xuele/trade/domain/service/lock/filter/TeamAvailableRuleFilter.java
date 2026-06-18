package cn.xuele.trade.domain.service.lock.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.ActivityTrialEntity;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;

import java.time.LocalDateTime;
import java.util.Objects;

import static cn.xuele.common.types.common.StringUtils.isBlank;

/**
 * 参团队伍可用性规则。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
public class TeamAvailableRuleFilter implements ILogicHandler<TradeLockCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> {

    private final ITradeRepository tradeRepository;

    public TeamAvailableRuleFilter(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradeLockResultEntity apply(TradeLockCommandEntity requestParameter,
                                       TradeLockRuleFilterFactory.DynamicContext dynamicContext) {
        if (isBlank(requestParameter.getTeamId())) {
            return null;
        }

        GroupBuyTeamEntity team = tradeRepository.queryTeamByTeamId(requestParameter.getTeamId());
        if (team == null) {
            throw new AppException(ResponseCode.TRADE_TEAM_NOT_AVAILABLE);
        }

        ActivityTrialEntity activityTrial = dynamicContext.getActivityTrial();
        if (!Objects.equals(team.getActivityId(), activityTrial.getActivityId())) {
            throw new AppException(ResponseCode.TRADE_TEAM_NOT_AVAILABLE);
        }
        if (!GroupBuyTeamStatusEnumVO.PROGRESS.equals(team.getStatus())) {
            throw new AppException(ResponseCode.TRADE_TEAM_NOT_AVAILABLE);
        }
        if (team.getValidEndTime() != null && !team.getValidEndTime().isAfter(LocalDateTime.now())) {
            throw new AppException(ResponseCode.TRADE_TEAM_NOT_AVAILABLE);
        }
        if (team.getLockCount() == null || team.getTargetCount() == null || team.getLockCount() >= team.getTargetCount()) {
            throw new AppException(ResponseCode.TRADE_TEAM_FULL);
        }

        dynamicContext.setTeam(team);
        return null;
    }
}
