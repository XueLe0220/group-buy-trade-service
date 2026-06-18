package cn.xuele.trade.infrastructure.adapter.repository;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.LockTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.NotifyConfigVO;
import cn.xuele.trade.domain.model.valobj.NotifyTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.infrastructure.dao.IGroupBuyOrderDao;
import cn.xuele.trade.infrastructure.dao.IGroupBuyOrderListDao;
import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrder;
import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrderList;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 交易仓储实现。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@Repository
public class TradeRepository implements ITradeRepository {

    private final IGroupBuyOrderDao groupBuyOrderDao;
    private final IGroupBuyOrderListDao groupBuyOrderListDao;

    public TradeRepository(IGroupBuyOrderDao groupBuyOrderDao, IGroupBuyOrderListDao groupBuyOrderListDao) {
        this.groupBuyOrderDao = groupBuyOrderDao;
        this.groupBuyOrderListDao = groupBuyOrderListDao;
    }

    @Override
    public TradeOrderEntity queryOrderByUserIdAndOutTradeNo(String userId, String outTradeNo) {
        GroupBuyOrderList orderList = groupBuyOrderListDao.queryByUserIdAndOutTradeNo(userId, outTradeNo);
        return toTradeOrderEntity(orderList);
    }

    @Override
    public Integer queryUserOrderCount(Long activityId, String userId) {
        return groupBuyOrderListDao.queryUserOrderCount(activityId, userId);
    }

    @Override
    public GroupBuyTeamEntity queryTeamByTeamId(String teamId) {
        return toGroupBuyTeamEntity(groupBuyOrderDao.queryByTeamId(teamId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TradeOrderEntity lockOrder(GroupBuyLockAggregate aggregate) {
        TradeOrderEntity order = aggregate.getOrder();

        TradeOrderEntity existOrder = queryOrderByUserIdAndOutTradeNo(order.getUserId(), order.getOutTradeNo());
        if (existOrder != null) {
            return existOrder;
        }

        Integer userOrderCount = queryUserOrderCount(order.getActivityId(), order.getUserId());
        if (aggregate.getTakeLimitCount() != null
                && aggregate.getTakeLimitCount() > 0
                && userOrderCount != null
                && userOrderCount >= aggregate.getTakeLimitCount()) {
            throw new AppException(ResponseCode.TRADE_TAKE_LIMIT);
        }
        String currentBizId = order.getActivityId() + "_" + order.getUserId() + "_" + ((userOrderCount == null ? 0 : userOrderCount) + 1);
        order.setBizId(currentBizId);

        if (LockTypeEnumVO.NEW_TEAM.equals(aggregate.getLockType())) {
            createNewTeam(aggregate);
        } else {
            joinExistingTeam(aggregate);
        }

        order.setOrderId(generateOrderId());
        order.setTeamId(aggregate.getTeam().getTeamId());

        try {
            groupBuyOrderListDao.insert(toGroupBuyOrderList(order));
            return order;
        } catch (DuplicateKeyException e) {
            TradeOrderEntity duplicateOrder = queryOrderByUserIdAndOutTradeNo(order.getUserId(), order.getOutTradeNo());
            if (duplicateOrder != null) {
                return duplicateOrder;
            }
            Integer currentUserOrderCount = queryUserOrderCount(order.getActivityId(), order.getUserId());
            if (aggregate.getTakeLimitCount() != null
                    && aggregate.getTakeLimitCount() > 0
                    && currentUserOrderCount != null
                    && currentUserOrderCount >= aggregate.getTakeLimitCount()) {
                throw new AppException(ResponseCode.TRADE_TAKE_LIMIT);
            }
            throw new AppException(ResponseCode.INDEX_EXCEPTION);
        }
    }

    private void createNewTeam(GroupBuyLockAggregate aggregate) {
        GroupBuyTeamEntity team = aggregate.getTeam();
        TradeOrderEntity order = aggregate.getOrder();
        team.setTeamId(generateTeamId());
        order.setTeamId(team.getTeamId());
        groupBuyOrderDao.insert(toGroupBuyOrder(team, order));
    }

    private void joinExistingTeam(GroupBuyLockAggregate aggregate) {
        int updateCount = groupBuyOrderDao.updateAddLockCount(aggregate.getTeam().getTeamId());
        if (updateCount != 1) {
            throw new AppException(ResponseCode.TRADE_TEAM_FULL);
        }
    }

    private String generateTeamId() {
        return "T" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
    }

    private String generateOrderId() {
        return "O" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
    }

    private GroupBuyOrder toGroupBuyOrder(GroupBuyTeamEntity team, TradeOrderEntity order) {
        NotifyConfigVO notifyConfig = team.getNotifyConfig();
        return GroupBuyOrder.builder()
                .teamId(team.getTeamId())
                .activityId(team.getActivityId())
                .activityName(team.getActivityName())
                .source(order.getSource())
                .channel(order.getChannel())
                .originalPrice(order.getOriginalPrice())
                .deductionPrice(order.getDeductionPrice())
                .payPrice(order.getPayPrice())
                .targetCount(team.getTargetCount())
                .lockCount(team.getLockCount())
                .completeCount(team.getCompleteCount())
                .status(team.getStatus().getStatus())
                .validStartTime(team.getValidStartTime())
                .validEndTime(team.getValidEndTime())
                .notifyType(notifyConfig == null || notifyConfig.getNotifyType() == null ? null : notifyConfig.getNotifyType().name())
                .notifyMQ(notifyConfig == null ? null : notifyConfig.getNotifyMQ())
                .notifyUrl(notifyConfig == null ? null : notifyConfig.getNotifyUrl())
                .build();
    }

    private GroupBuyOrderList toGroupBuyOrderList(TradeOrderEntity order) {
        return GroupBuyOrderList.builder()
                .userId(order.getUserId())
                .teamId(order.getTeamId())
                .orderId(order.getOrderId())
                .activityId(order.getActivityId())
                .activityName(order.getActivityName())
                .goodsId(order.getGoodsId())
                .goodsName(order.getGoodsName())
                .source(order.getSource())
                .channel(order.getChannel())
                .originalPrice(order.getOriginalPrice())
                .deductionPrice(order.getDeductionPrice())
                .payPrice(order.getPayPrice())
                .status(order.getStatus().getStatus())
                .outTradeNo(order.getOutTradeNo())
                .bizId(order.getBizId())
                .payNo(order.getPayNo())
                .payAmount(order.getPayAmount())
                .payTime(order.getPayTime())
                .build();
    }

    private TradeOrderEntity toTradeOrderEntity(GroupBuyOrderList orderList) {
        if (orderList == null) {
            return null;
        }
        return TradeOrderEntity.builder()
                .userId(orderList.getUserId())
                .teamId(orderList.getTeamId())
                .orderId(orderList.getOrderId())
                .activityId(orderList.getActivityId())
                .activityName(orderList.getActivityName())
                .goodsId(orderList.getGoodsId())
                .goodsName(orderList.getGoodsName())
                .source(orderList.getSource())
                .channel(orderList.getChannel())
                .originalPrice(orderList.getOriginalPrice())
                .deductionPrice(orderList.getDeductionPrice())
                .payPrice(orderList.getPayPrice())
                .status(valueOfTradeOrderStatus(orderList.getStatus()))
                .outTradeNo(orderList.getOutTradeNo())
                .bizId(orderList.getBizId())
                .payNo(orderList.getPayNo())
                .payAmount(orderList.getPayAmount())
                .payTime(orderList.getPayTime())
                .createTime(orderList.getCreateTime())
                .updateTime(orderList.getUpdateTime())
                .build();
    }

    private GroupBuyTeamEntity toGroupBuyTeamEntity(GroupBuyOrder order) {
        if (order == null) {
            return null;
        }
        return GroupBuyTeamEntity.builder()
                .teamId(order.getTeamId())
                .activityId(order.getActivityId())
                .activityName(order.getActivityName())
                .targetCount(order.getTargetCount())
                .lockCount(order.getLockCount())
                .completeCount(order.getCompleteCount())
                .validStartTime(order.getValidStartTime())
                .validEndTime(order.getValidEndTime())
                .status(valueOfTeamStatus(order.getStatus()))
                .notifyConfig(NotifyConfigVO.builder()
                        .notifyType(order.getNotifyType() == null ? null : NotifyTypeEnumVO.valueOf(order.getNotifyType()))
                        .notifyMQ(order.getNotifyMQ())
                        .notifyUrl(order.getNotifyUrl())
                        .build())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .build();
    }

    private TradeOrderStatusEnumVO valueOfTradeOrderStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (TradeOrderStatusEnumVO item : TradeOrderStatusEnumVO.values()) {
            if (item.getStatus() == status) {
                return item;
            }
        }
        throw new AppException(ResponseCode.UN_ERROR.getCode(), "未知交易订单状态: " + status);
    }

    private GroupBuyTeamStatusEnumVO valueOfTeamStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (GroupBuyTeamStatusEnumVO item : GroupBuyTeamStatusEnumVO.values()) {
            if (item.getStatus() == status) {
                return item;
            }
        }
        throw new AppException(ResponseCode.UN_ERROR.getCode(), "未知拼团队伍状态: " + status);
    }
}
