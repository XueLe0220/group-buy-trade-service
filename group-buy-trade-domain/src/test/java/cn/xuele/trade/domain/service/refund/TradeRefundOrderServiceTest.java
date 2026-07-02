package cn.xuele.trade.domain.service.refund;

import cn.xuele.common.design.framework.link.chain.BusinessLinkedList;
import cn.xuele.trade.domain.adapter.port.ITeamStockReservationPort;
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
import cn.xuele.trade.domain.model.entity.command.TradeRefundCommandEntity;
import cn.xuele.trade.domain.model.valobj.GroupBuyTeamStatusEnumVO;
import cn.xuele.trade.domain.model.valobj.RefundTypeEnumVO;
import cn.xuele.trade.domain.model.valobj.TradeOrderStatusEnumVO;
import cn.xuele.trade.domain.service.refund.factory.TradeRefundRuleFilterFactory;
import cn.xuele.trade.domain.service.refund.filter.RefundBuildRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundIdempotentRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundOrderLoadRuleFilter;
import cn.xuele.trade.domain.service.refund.filter.RefundTypeRuleFilter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeRefundOrderServiceTest {

    @Test
    void recoversTeamStockAfterUnpaidRefund() throws Exception {
        RecordingTeamStockReservationPort teamStockReservationPort = new RecordingTeamStockReservationPort();
        StubTradeRepository tradeRepository = repositoryWith(
                TradeOrderStatusEnumVO.CREATE,
                GroupBuyTeamStatusEnumVO.PROGRESS);
        TradeRefundOrderService service = refundService(tradeRepository, teamStockReservationPort);

        TradeRefundResultEntity result = service.refundTradeOrder(command());

        assertEquals(RefundTypeEnumVO.UNPAID, result.getRefundType());
        assertEquals(List.of(new RecoveryCall("T1001", "refund-recover:O1001", tradeRepository.team.getValidEndTime())),
                teamStockReservationPort.recoveryCalls);
    }

    @Test
    void recoversTeamStockAfterPaidUnformedRefund() throws Exception {
        RecordingTeamStockReservationPort teamStockReservationPort = new RecordingTeamStockReservationPort();
        StubTradeRepository tradeRepository = repositoryWith(
                TradeOrderStatusEnumVO.COMPLETE,
                GroupBuyTeamStatusEnumVO.PROGRESS);
        TradeRefundOrderService service = refundService(tradeRepository, teamStockReservationPort);

        TradeRefundResultEntity result = service.refundTradeOrder(command());

        assertEquals(RefundTypeEnumVO.PAID_UNFORMED, result.getRefundType());
        assertEquals(1, teamStockReservationPort.recoveryCalls.size());
        assertEquals("refund-recover:O1001", teamStockReservationPort.recoveryCalls.get(0).recoveryBizId());
    }

    @Test
    void doesNotRecoverTeamStockAfterPaidFormedRefund() throws Exception {
        RecordingTeamStockReservationPort teamStockReservationPort = new RecordingTeamStockReservationPort();
        StubTradeRepository tradeRepository = repositoryWith(
                TradeOrderStatusEnumVO.COMPLETE,
                GroupBuyTeamStatusEnumVO.COMPLETE);
        TradeRefundOrderService service = refundService(tradeRepository, teamStockReservationPort);

        TradeRefundResultEntity result = service.refundTradeOrder(command());

        assertEquals(RefundTypeEnumVO.PAID_FORMED, result.getRefundType());
        assertEquals(0, teamStockReservationPort.recoveryCalls.size());
    }

    @Test
    void recoversTeamStockIdempotentlyWhenRepeatRefundAlreadyClosedProgressOrder() throws Exception {
        RecordingTeamStockReservationPort teamStockReservationPort = new RecordingTeamStockReservationPort();
        StubTradeRepository tradeRepository = repositoryWith(
                TradeOrderStatusEnumVO.CLOSE,
                GroupBuyTeamStatusEnumVO.PROGRESS);
        TradeRefundOrderService service = refundService(tradeRepository, teamStockReservationPort);

        TradeRefundResultEntity result = service.refundTradeOrder(command());

        assertTrue(result.isIdempotentHit());
        assertEquals(List.of(new RecoveryCall("T1001", "refund-recover:O1001", tradeRepository.team.getValidEndTime())),
                teamStockReservationPort.recoveryCalls);
    }

    private static TradeRefundOrderService refundService(ITradeRepository tradeRepository,
                                                         ITeamStockReservationPort teamStockReservationPort) {
        BusinessLinkedList<TradeRefundCommandEntity, TradeRefundRuleFilterFactory.DynamicContext,
                TradeRefundResultEntity> ruleFilter = new TradeRefundRuleFilterFactory().tradeRefundRuleFilter(
                new RefundOrderLoadRuleFilter(tradeRepository),
                new RefundIdempotentRuleFilter(),
                new RefundTypeRuleFilter(),
                new RefundBuildRuleFilter());
        return new TradeRefundOrderService(tradeRepository, teamStockReservationPort, ruleFilter);
    }

    private static StubTradeRepository repositoryWith(TradeOrderStatusEnumVO orderStatus,
                                                      GroupBuyTeamStatusEnumVO teamStatus) {
        LocalDateTime validEndTime = LocalDateTime.now().plusMinutes(30);
        TradeOrderEntity order = TradeOrderEntity.builder()
                .userId("U1001")
                .orderId("O1001")
                .outTradeNo("OUT1001")
                .teamId("T1001")
                .source("s01")
                .channel("c01")
                .activityId(1001L)
                .status(orderStatus)
                .build();
        GroupBuyTeamEntity team = GroupBuyTeamEntity.builder()
                .teamId("T1001")
                .activityId(1001L)
                .targetCount(3)
                .lockCount(2)
                .completeCount(TradeOrderStatusEnumVO.COMPLETE.equals(orderStatus) ? 1 : 0)
                .validEndTime(validEndTime)
                .status(teamStatus)
                .build();
        return new StubTradeRepository(order, team);
    }

    private static TradeRefundCommandEntity command() {
        return TradeRefundCommandEntity.builder()
                .userId("U1001")
                .outTradeNo("OUT1001")
                .source("s01")
                .channel("c01")
                .refundNo("R1001")
                .refundReason("test refund")
                .build();
    }

    private record RecoveryCall(String teamId, String recoveryBizId, LocalDateTime validEndTime) {
    }

    private static class RecordingTeamStockReservationPort implements ITeamStockReservationPort {

        private final List<RecoveryCall> recoveryCalls = new ArrayList<>();

        @Override
        public boolean reserve(String teamId, int targetCount, int currentLockCount, LocalDateTime validEndTime) {
            return true;
        }

        @Override
        public void recover(String teamId, String recoveryBizId, LocalDateTime validEndTime) {
            recoveryCalls.add(new RecoveryCall(teamId, recoveryBizId, validEndTime));
        }
    }

    private static class StubTradeRepository implements ITradeRepository {

        private final TradeOrderEntity order;
        private final GroupBuyTeamEntity team;

        private StubTradeRepository(TradeOrderEntity order, GroupBuyTeamEntity team) {
            this.order = order;
            this.team = team;
        }

        @Override
        public TradeOrderEntity queryOrderByUserIdAndOutTradeNo(String userId, String outTradeNo) {
            return order;
        }

        @Override
        public TradeOrderEntity queryOrderByPayNo(String payNo) {
            return null;
        }

        @Override
        public Integer queryUserOrderCount(Long activityId, String userId) {
            return 0;
        }

        @Override
        public GroupBuyTeamEntity queryTeamByTeamId(String teamId) {
            return team;
        }

        @Override
        public TradeLockOrderResultEntity lockOrder(GroupBuyLockAggregate aggregate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TradePayOrderResultEntity preparePayOrder(TradeOrderEntity order, GroupBuyTeamEntity team) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TradeSettlementResultEntity settlementOrder(GroupBuySettlementAggregate aggregate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TradeRefundResultEntity refundOrder(GroupBuyRefundAggregate aggregate) {
            return TradeRefundResultEntity.builder()
                    .order(order)
                    .team(team)
                    .refundType(aggregate.getRefundType())
                    .build();
        }
    }
}
