package cn.xuele.trade.infrastructure.adapter.repository;

import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.trade.domain.adapter.repository.ITradeRepository;
import cn.xuele.trade.domain.model.aggregate.GroupBuyLockAggregate;
import cn.xuele.trade.domain.model.aggregate.GroupBuyRefundAggregate;
import cn.xuele.trade.domain.model.aggregate.GroupBuySettlementAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeLockOrderResultEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradePayOrderResultEntity;
import cn.xuele.trade.domain.model.entity.TradeRefundResultEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.LockTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.NotifyConfigVO;
import cn.xuele.trade.domain.model.valobj.NotifyTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.infrastructure.dao.IGroupBuyOrderDao;
import cn.xuele.trade.infrastructure.dao.IGroupBuyOrderListDao;
import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrder;
import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrderList;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
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
    public TradeOrderEntity queryOrderByPayNo(String payNo) {
        if (payNo == null || payNo.isBlank()) {
            return null;
        }
        return toTradeOrderEntity(groupBuyOrderListDao.queryByPayNo(payNo));
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
    public TradeLockOrderResultEntity lockOrder(GroupBuyLockAggregate aggregate) {
        TradeOrderEntity order = aggregate.getOrder();

        TradeOrderEntity existOrder = queryOrderByUserIdAndOutTradeNo(order.getUserId(), order.getOutTradeNo());
        if (existOrder != null) {
            return TradeLockOrderResultEntity.builder()
                    .order(existOrder)
                    .idempotentHit(true)
                    .build();
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
            return TradeLockOrderResultEntity.builder()
                    .order(order)
                    .created(true)
                    .build();
        } catch (DuplicateKeyException e) {
            TradeOrderEntity duplicateOrder = queryOrderByUserIdAndOutTradeNo(order.getUserId(), order.getOutTradeNo());
            if (duplicateOrder != null) {
                throw new AppException(ResponseCode.INDEX_EXCEPTION.getCode(), ResponseCode.INDEX_EXCEPTION.getInfo(), e);
            }
            Integer currentUserOrderCount = queryUserOrderCount(order.getActivityId(), order.getUserId());
            if (aggregate.getTakeLimitCount() != null
                    && aggregate.getTakeLimitCount() > 0
                    && currentUserOrderCount != null
                    && currentUserOrderCount >= aggregate.getTakeLimitCount()) {
                throw new AppException(ResponseCode.TRADE_TAKE_LIMIT);
            }
            throw new AppException(ResponseCode.INDEX_EXCEPTION.getCode(), ResponseCode.INDEX_EXCEPTION.getInfo(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TradePayOrderResultEntity preparePayOrder(TradeOrderEntity order, GroupBuyTeamEntity team) {
        TradeOrderEntity currentOrder = toTradeOrderEntity(groupBuyOrderListDao.queryByUserIdAndOutTradeNoForUpdate(
                order.getUserId(),
                order.getOutTradeNo()));
        if (currentOrder == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单不存在");
        }
        if (!TradeOrderStatusEnumVO.CREATE.equals(currentOrder.getStatus())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单状态不可发起支付");
        }
        if (currentOrder.getPaymentRequestNo() == null || currentOrder.getPaymentRequestNo().isBlank()) {
            GroupBuyOrderList updateOrder = GroupBuyOrderList.builder()
                    .userId(currentOrder.getUserId())
                    .outTradeNo(currentOrder.getOutTradeNo())
                    .paymentRequestNo(generatePaymentRequestNo())
                    .paymentRequestTime(LocalDateTime.now())
                    .build();
            int updateCount = groupBuyOrderListDao.updatePaymentRequest(updateOrder);
            if (updateCount != 1) {
                throw new AppException(ResponseCode.UPDATE_ZERO);
            }
            currentOrder = queryOrderByUserIdAndOutTradeNo(currentOrder.getUserId(), currentOrder.getOutTradeNo());
        }
        return TradePayOrderResultEntity.builder()
                .order(currentOrder)
                .team(team)
                .paymentRequestNo(currentOrder.getPaymentRequestNo())
                .payExpireTime(team.getValidEndTime())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TradeSettlementResultEntity settlementOrder(GroupBuySettlementAggregate aggregate) {
        TradeSettlementCommandEntity command = aggregate.getCommand();
        TradeOrderEntity order = aggregate.getOrder();

        TradeOrderEntity currentOrder = toTradeOrderEntity(groupBuyOrderListDao.queryByUserIdAndOutTradeNoForUpdate(
                order.getUserId(),
                order.getOutTradeNo()));
        if (currentOrder == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单不存在");
        }
        if (TradeOrderStatusEnumVO.COMPLETE.equals(currentOrder.getStatus())) {
            if (Objects.equals(currentOrder.getPayNo(), command.getPayNo())
                    && currentOrder.getPaidAmount() != null
                    && currentOrder.getPaidAmount().compareTo(command.getPaidAmount()) == 0) {
                return TradeSettlementResultEntity.builder()
                        .order(currentOrder)
                        .team(queryTeamByTeamId(currentOrder.getTeamId()))
                        .idempotentHit(true)
                        .build();
            }
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单已支付，但支付流水或金额不一致");
        }
        if (!TradeOrderStatusEnumVO.CREATE.equals(currentOrder.getStatus())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单状态不可结算");
        }

        TradeOrderEntity payNoOrder = queryOrderByPayNo(command.getPayNo());
        if (payNoOrder != null && !Objects.equals(payNoOrder.getOrderId(), currentOrder.getOrderId())) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "支付流水号已绑定其他交易订单");
        }

        GroupBuyOrderList updateOrder = GroupBuyOrderList.builder()
                .userId(command.getUserId())
                .outTradeNo(command.getOutTradeNo())
                .payNo(command.getPayNo())
                .paidAmount(command.getPaidAmount())
                .payTime(command.getPayTime())
                .build();
        int updateOrderCount;
        try {
            updateOrderCount = groupBuyOrderListDao.updateOrderStatusComplete(updateOrder);
        } catch (DuplicateKeyException e) {
            TradeOrderEntity duplicateOrder = queryOrderByPayNo(command.getPayNo());
            if (duplicateOrder != null && Objects.equals(duplicateOrder.getOrderId(), currentOrder.getOrderId())) {
                return TradeSettlementResultEntity.builder()
                        .order(duplicateOrder)
                        .team(queryTeamByTeamId(duplicateOrder.getTeamId()))
                        .idempotentHit(true)
                        .build();
            }
            throw new AppException(ResponseCode.INDEX_EXCEPTION);
        }
        if (updateOrderCount != 1) {
            TradeOrderEntity afterUpdateOrder = queryOrderByUserIdAndOutTradeNo(command.getUserId(), command.getOutTradeNo());
            if (afterUpdateOrder != null
                    && TradeOrderStatusEnumVO.COMPLETE.equals(afterUpdateOrder.getStatus())
                    && Objects.equals(afterUpdateOrder.getPayNo(), command.getPayNo())
                    && afterUpdateOrder.getPaidAmount() != null
                    && afterUpdateOrder.getPaidAmount().compareTo(command.getPaidAmount()) == 0) {
                return TradeSettlementResultEntity.builder()
                        .order(afterUpdateOrder)
                        .team(queryTeamByTeamId(afterUpdateOrder.getTeamId()))
                        .idempotentHit(true)
                        .build();
            }
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }

        int updateTeamCount = groupBuyOrderDao.updateAddCompleteCount(currentOrder.getTeamId());
        if (updateTeamCount != 1) {
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }
        groupBuyOrderDao.updateTeamStatusComplete(currentOrder.getTeamId());

        TradeOrderEntity settledOrder = queryOrderByUserIdAndOutTradeNo(command.getUserId(), command.getOutTradeNo());
        GroupBuyTeamEntity settledTeam = queryTeamByTeamId(currentOrder.getTeamId());
        return TradeSettlementResultEntity.builder()
                .order(settledOrder)
                .team(settledTeam)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TradeRefundResultEntity refundOrder(GroupBuyRefundAggregate aggregate) {
        TradeOrderEntity order = aggregate.getOrder();
        TradeOrderEntity currentOrder = toTradeOrderEntity(groupBuyOrderListDao.queryByUserIdAndOutTradeNoForUpdate(
                order.getUserId(),
                order.getOutTradeNo()));
        if (currentOrder == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "交易订单不存在");
        }
        GroupBuyTeamEntity currentTeam = toGroupBuyTeamEntity(groupBuyOrderDao.queryByTeamIdForUpdate(currentOrder.getTeamId()));
        if (currentTeam == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "拼团队伍不存在");
        }
        if (TradeOrderStatusEnumVO.CLOSE.equals(currentOrder.getStatus())) {
            return TradeRefundResultEntity.builder()
                    .order(currentOrder)
                    .team(currentTeam)
                    .idempotentHit(true)
                    .build();
        }

        RefundTypeEnumVO currentRefundType = RefundTypeEnumVO.getRefundType(currentTeam.getStatus(), currentOrder.getStatus());
        GroupBuyTeamStatusEnumVO targetTeamStatus = resolveRefundTargetTeamStatus(currentRefundType, currentTeam);

        GroupBuyOrderList updateOrder = GroupBuyOrderList.builder()
                .userId(currentOrder.getUserId())
                .outTradeNo(currentOrder.getOutTradeNo())
                .status(currentOrder.getStatus().getStatus())
                .build();
        int updateOrderCount = groupBuyOrderListDao.updateOrderStatusClose(updateOrder);
        if (updateOrderCount != 1) {
            TradeOrderEntity afterUpdateOrder = queryOrderByUserIdAndOutTradeNo(currentOrder.getUserId(), currentOrder.getOutTradeNo());
            if (afterUpdateOrder != null && TradeOrderStatusEnumVO.CLOSE.equals(afterUpdateOrder.getStatus())) {
                return TradeRefundResultEntity.builder()
                        .order(afterUpdateOrder)
                        .team(queryTeamByTeamId(afterUpdateOrder.getTeamId()))
                        .refundType(currentRefundType)
                        .idempotentHit(true)
                        .build();
            }
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }

        int updateTeamCount = switch (currentRefundType) {
            case UNPAID_UNLOCK -> groupBuyOrderDao.updateUnpaidRefund(currentOrder.getTeamId());
            case PAID_UNFORMED -> groupBuyOrderDao.updatePaidUnformedRefund(currentOrder.getTeamId());
            case PAID_FORMED -> groupBuyOrderDao.updatePaidFormedRefund(currentOrder.getTeamId(), targetTeamStatus.getStatus());
        };
        if (updateTeamCount != 1) {
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }

        TradeOrderEntity refundedOrder = queryOrderByUserIdAndOutTradeNo(currentOrder.getUserId(), currentOrder.getOutTradeNo());
        GroupBuyTeamEntity refundedTeam = queryTeamByTeamId(currentOrder.getTeamId());
        return TradeRefundResultEntity.builder()
                .order(refundedOrder)
                .team(refundedTeam)
                .refundType(currentRefundType)
                .build();
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

    private String generatePaymentRequestNo() {
        return "P" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
    }

    private GroupBuyTeamStatusEnumVO resolveRefundTargetTeamStatus(RefundTypeEnumVO refundType, GroupBuyTeamEntity team) {
        if (!RefundTypeEnumVO.PAID_FORMED.equals(refundType)) {
            return team.getStatus();
        }
        Integer completeCount = team.getCompleteCount();
        if (completeCount == null || completeCount <= 1) {
            return GroupBuyTeamStatusEnumVO.FAIL;
        }
        return GroupBuyTeamStatusEnumVO.PARTIAL_REFUND;
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
                .payableAmount(order.getPayableAmount())
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
                .payableAmount(order.getPayableAmount())
                .status(order.getStatus().getStatus())
                .outTradeNo(order.getOutTradeNo())
                .bizId(order.getBizId())
                .paymentRequestNo(order.getPaymentRequestNo())
                .paymentRequestTime(order.getPaymentRequestTime())
                .payNo(order.getPayNo())
                .paidAmount(order.getPaidAmount())
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
                .payableAmount(orderList.getPayableAmount())
                .status(valueOfTradeOrderStatus(orderList.getStatus()))
                .outTradeNo(orderList.getOutTradeNo())
                .bizId(orderList.getBizId())
                .paymentRequestNo(orderList.getPaymentRequestNo())
                .paymentRequestTime(orderList.getPaymentRequestTime())
                .payNo(orderList.getPayNo())
                .paidAmount(orderList.getPaidAmount())
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
