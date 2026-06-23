package cn.xuele.trade.domain.service.lock.filter;

import cn.xuele.common.design.framework.link.handler.ILogicHandler;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.port.ITeamStockReservationPort;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeLockResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import cn.xuele.trade.domain.service.lock.factory.TradeLockRuleFilterFactory;


import java.util.UUID;

import static cn.xuele.common.types.common.StringUtils.isBlank;

/**
 * 锁单库存预占
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/23 10:44
 */
public class TeamStockReserveRuleFilter implements ILogicHandler<TradeLockCommandEntity,
        TradeLockRuleFilterFactory.DynamicContext, TradeLockResultEntity> {

    private final ITeamStockReservationPort teamStockReservationPort;

    public TeamStockReserveRuleFilter(ITeamStockReservationPort teamStockReservationPort) {
        this.teamStockReservationPort = teamStockReservationPort;
    }

    @Override
    public TradeLockResultEntity apply(TradeLockCommandEntity tradeLockCommandEntity,
                                       TradeLockRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        String teamId = tradeLockCommandEntity.getTeamId();

        if (isBlank(teamId)) return null;

        GroupBuyTeamEntity team = dynamicContext.getTeam();
        boolean reserved = teamStockReservationPort.reserve(teamId, team.getTargetCount(),team.getLockCount(), team.getValidEndTime());

        if (!reserved) {
            throw new AppException(ResponseCode.TRADE_TEAM_FULL);
        }
        dynamicContext.setTeamStockReserved(true);
        dynamicContext.setTeamStockRecoveryBizId(
                "lock-recover:" + tradeLockCommandEntity.getOutTradeNo() + ":" + UUID.randomUUID()
        );

        return null;
    }

}
