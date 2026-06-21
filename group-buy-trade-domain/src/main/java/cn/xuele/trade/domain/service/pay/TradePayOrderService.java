package cn.xuele.trade.domain.service.pay;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradePayOrderResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradePayCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.domain.service.ITradePayOrderService;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 交易发起支付领域服务实现。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/21
 */
public class TradePayOrderService implements ITradePayOrderService {

    private final ITradeRepository tradeRepository;

    public TradePayOrderService(ITradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Override
    public TradePayOrderResultEntity prepareTradePayOrder(TradePayCommandEntity command) {
        TradeOrderEntity order = tradeRepository.queryOrderByUserIdAndOutTradeNo(command.getUserId(), command.getOutTradeNo());
        if (order == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单不存在");
        }
        if (!Objects.equals(order.getSource(), command.getSource())
                || !Objects.equals(order.getChannel(), command.getChannel())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单来源或渠道不一致");
        }
        if (!TradeOrderStatusEnumVO.CREATE.equals(order.getStatus())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单状态不可发起支付");
        }

        GroupBuyTeamEntity team = tradeRepository.queryTeamByTeamId(order.getTeamId());
        if (team == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍不存在");
        }
        if (!Objects.equals(team.getActivityId(), order.getActivityId())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单与拼团队伍活动不一致");
        }
        if (!GroupBuyTeamStatusEnumVO.PROGRESS.equals(team.getStatus())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍状态不可发起支付");
        }
        if (team.getValidEndTime() != null && !team.getValidEndTime().isAfter(LocalDateTime.now())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍已过期，不能发起支付");
        }

        return tradeRepository.preparePayOrder(order, team);
    }
}
