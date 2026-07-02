package cn.xuele.trade.infrastructure.adapter.repository;

import cn.xuele.trade.domain.model.aggregate.GroupBuySettlementAggregate;
import cn.xuele.trade.domain.model.entity.GroupBuyTeamEntity;
import cn.xuele.trade.domain.model.entity.TradeOrderEntity;
import cn.xuele.trade.domain.model.entity.TradeSettlementResultEntity;
import cn.xuele.trade.domain.model.entity.command.TradeSettlementCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.NotifyTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.infrastructure.dao.IGroupBuyOrderDao;
import cn.xuele.trade.infrastructure.dao.IGroupBuyOrderListDao;
import cn.xuele.trade.infrastructure.dao.ITradeEventOutboxDao;
import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrder;
import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrderList;
import cn.xuele.trade.infrastructure.dao.po.TradeEventOutbox;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeRepositoryOutboxTest {

    @Test
    void writesTeamCompletedEventWhenSettlementCompletesTeamForFirstTime() {
        InMemoryGroupBuyOrderDao orderDao = new InMemoryGroupBuyOrderDao();
        InMemoryGroupBuyOrderListDao orderListDao = new InMemoryGroupBuyOrderListDao();
        RecordingTradeEventOutboxDao outboxDao = new RecordingTradeEventOutboxDao();
        orderDao.team = progressTeam(2, 1);
        orderListDao.orders.add(createOrder("U001", "OT001", "O001"));
        orderListDao.orders.add(completeOrder("U002", "OT002", "O002"));
        TradeRepository repository = new TradeRepository(orderDao, orderListDao, outboxDao);

        TradeSettlementResultEntity result = repository.settlementOrder(settlementAggregate("U001", "OT001"));

        assertEquals(GroupBuyTeamStatusEnumVO.COMPLETE, result.getTeam().getStatus());
        assertEquals(1, outboxDao.events.size());
        TradeEventOutbox event = outboxDao.events.get(0);
        assertEquals("TEAM_COMPLETED", event.getEventType());
        assertEquals("GROUP_BUY_TEAM", event.getAggregateType());
        assertEquals("T1001", event.getAggregateId());
        assertEquals("TEAM_COMPLETED:T1001", event.getBizId());
        assertEquals("group-buy.trade.events", event.getTopic());
        assertEquals(0, event.getStatus());
        assertTrue(event.getPayloadJson().contains("\"teamId\":\"T1001\""));
        assertTrue(event.getPayloadJson().contains("\"outTradeNoList\":[\"OT001\",\"OT002\"]"));
        assertTrue(event.getPayloadJson().contains("\"notifyType\":\"HTTP\""));
    }

    @Test
    void doesNotWriteTeamCompletedEventWhenSettlementIsIdempotentHit() {
        InMemoryGroupBuyOrderDao orderDao = new InMemoryGroupBuyOrderDao();
        InMemoryGroupBuyOrderListDao orderListDao = new InMemoryGroupBuyOrderListDao();
        RecordingTradeEventOutboxDao outboxDao = new RecordingTradeEventOutboxDao();
        orderDao.team = completedTeam(2, 2);
        GroupBuyOrderList completedOrder = completeOrder("U001", "OT001", "O001");
        completedOrder.setPayNo("PAY001");
        completedOrder.setPaidAmount(new BigDecimal("90.00"));
        orderListDao.orders.add(completedOrder);
        TradeRepository repository = new TradeRepository(orderDao, orderListDao, outboxDao);

        TradeSettlementResultEntity result = repository.settlementOrder(settlementAggregate("U001", "OT001"));

        assertTrue(result.isIdempotentHit());
        assertEquals(0, outboxDao.events.size());
    }

    private static GroupBuySettlementAggregate settlementAggregate(String userId, String outTradeNo) {
        return GroupBuySettlementAggregate.builder()
                .command(TradeSettlementCommandEntity.builder()
                        .userId(userId)
                        .outTradeNo(outTradeNo)
                        .payNo("PAY001")
                        .paidAmount(new BigDecimal("90.00"))
                        .payTime(LocalDateTime.now())
                        .build())
                .order(TradeOrderEntity.builder()
                        .userId(userId)
                        .outTradeNo(outTradeNo)
                        .build())
                .team(GroupBuyTeamEntity.builder()
                        .teamId("T1001")
                        .build())
                .build();
    }

    private static GroupBuyOrder progressTeam(int targetCount, int completeCount) {
        return team(targetCount, completeCount, 0);
    }

    private static GroupBuyOrder completedTeam(int targetCount, int completeCount) {
        return team(targetCount, completeCount, 1);
    }

    private static GroupBuyOrder team(int targetCount, int completeCount, int status) {
        return GroupBuyOrder.builder()
                .teamId("T1001")
                .activityId(100123L)
                .activityName("activity")
                .source("s01")
                .channel("c01")
                .originalPrice(new BigDecimal("100.00"))
                .deductionPrice(new BigDecimal("10.00"))
                .payableAmount(new BigDecimal("90.00"))
                .targetCount(targetCount)
                .lockCount(targetCount)
                .completeCount(completeCount)
                .status(status)
                .validStartTime(LocalDateTime.now().minusMinutes(5))
                .validEndTime(LocalDateTime.now().plusMinutes(10))
                .notifyType(NotifyTypeEnumVO.HTTP.name())
                .notifyUrl("http://127.0.0.1:8091/api/v1/test/group_buy_notify")
                .build();
    }

    private static GroupBuyOrderList createOrder(String userId, String outTradeNo, String orderId) {
        return order(userId, outTradeNo, orderId, TradeOrderStatusEnumVO.CREATE.getStatus());
    }

    private static GroupBuyOrderList completeOrder(String userId, String outTradeNo, String orderId) {
        return order(userId, outTradeNo, orderId, TradeOrderStatusEnumVO.COMPLETE.getStatus());
    }

    private static GroupBuyOrderList order(String userId, String outTradeNo, String orderId, int status) {
        return GroupBuyOrderList.builder()
                .userId(userId)
                .teamId("T1001")
                .orderId(orderId)
                .activityId(100123L)
                .activityName("activity")
                .goodsId("G001")
                .goodsName("goods")
                .source("s01")
                .channel("c01")
                .originalPrice(new BigDecimal("100.00"))
                .deductionPrice(new BigDecimal("10.00"))
                .payableAmount(new BigDecimal("90.00"))
                .status(status)
                .outTradeNo(outTradeNo)
                .bizId("BIZ:" + orderId)
                .build();
    }

    private static class RecordingTradeEventOutboxDao implements ITradeEventOutboxDao {

        private final List<TradeEventOutbox> events = new ArrayList<>();

        @Override
        public int insert(TradeEventOutbox event) {
            assertNotNull(event.getEventId());
            assertNotNull(event.getPayloadJson());
            assertFalse(event.getPayloadJson().isBlank());
            events.add(event);
            return 1;
        }

        @Override
        public List<TradeEventOutbox> queryPendingEvents(int limit) {
            return Collections.emptyList();
        }

        @Override
        public int markSent(String eventId, LocalDateTime sentTime) {
            return 0;
        }

        @Override
        public int markRetry(String eventId, int retryCount, LocalDateTime nextRetryTime, String lastError) {
            return 0;
        }

        @Override
        public int markFailed(String eventId, int retryCount, String lastError) {
            return 0;
        }
    }

    private static class InMemoryGroupBuyOrderDao implements IGroupBuyOrderDao {

        private GroupBuyOrder team;

        @Override
        public int insert(GroupBuyOrder groupBuyOrder) {
            this.team = groupBuyOrder;
            return 1;
        }

        @Override
        public int updateAddLockCount(String teamId) {
            return 0;
        }

        @Override
        public int updateAddCompleteCount(String teamId) {
            if (team == null || !team.getTeamId().equals(teamId) || team.getStatus() != 0 || team.getCompleteCount() >= team.getTargetCount()) {
                return 0;
            }
            team.setCompleteCount(team.getCompleteCount() + 1);
            return 1;
        }

        @Override
        public int updateTeamStatusComplete(String teamId) {
            if (team == null || !team.getTeamId().equals(teamId) || team.getStatus() != 0 || team.getCompleteCount() < team.getTargetCount()) {
                return 0;
            }
            team.setStatus(1);
            return 1;
        }

        @Override
        public int updateUnpaidRefund(String teamId) {
            return 0;
        }

        @Override
        public int updatePaidUnformedRefund(String teamId) {
            return 0;
        }

        @Override
        public int updatePaidFormedRefund(String teamId, Integer status) {
            return 0;
        }

        @Override
        public GroupBuyOrder queryByTeamId(String teamId) {
            return team;
        }

        @Override
        public GroupBuyOrder queryByTeamIdForUpdate(String teamId) {
            return team;
        }
    }

    private static class InMemoryGroupBuyOrderListDao implements IGroupBuyOrderListDao {

        private final List<GroupBuyOrderList> orders = new ArrayList<>();

        @Override
        public int insert(GroupBuyOrderList groupBuyOrderList) {
            orders.add(groupBuyOrderList);
            return 1;
        }

        @Override
        public GroupBuyOrderList queryByUserIdAndOutTradeNo(String userId, String outTradeNo) {
            return orders.stream()
                    .filter(order -> order.getUserId().equals(userId) && order.getOutTradeNo().equals(outTradeNo))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public GroupBuyOrderList queryByUserIdAndOutTradeNoForUpdate(String userId, String outTradeNo) {
            return queryByUserIdAndOutTradeNo(userId, outTradeNo);
        }

        @Override
        public GroupBuyOrderList queryByPayNo(String payNo) {
            return orders.stream()
                    .filter(order -> payNo.equals(order.getPayNo()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<String> queryCompleteOutTradeNoListByTeamId(String teamId) {
            return orders.stream()
                    .filter(order -> teamId.equals(order.getTeamId()))
                    .filter(order -> order.getStatus() == 1)
                    .map(GroupBuyOrderList::getOutTradeNo)
                    .toList();
        }

        @Override
        public Integer queryUserOrderCount(Long activityId, String userId) {
            return 0;
        }

        @Override
        public int updatePaymentRequest(GroupBuyOrderList groupBuyOrderList) {
            return 0;
        }

        @Override
        public int updateOrderStatusComplete(GroupBuyOrderList updateOrder) {
            GroupBuyOrderList order = queryByUserIdAndOutTradeNo(updateOrder.getUserId(), updateOrder.getOutTradeNo());
            if (order == null || order.getStatus() != 0) {
                return 0;
            }
            order.setStatus(1);
            order.setPayNo(updateOrder.getPayNo());
            order.setPaidAmount(updateOrder.getPaidAmount());
            order.setPayTime(updateOrder.getPayTime());
            return 1;
        }

        @Override
        public int updateOrderStatusClose(GroupBuyOrderList groupBuyOrderList) {
            return 0;
        }
    }
}
