package cn.xuele.trade.infrastructure.dao;

import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrderList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户交易订单 DAO。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@Mapper
public interface IGroupBuyOrderListDao {

    int insert(GroupBuyOrderList groupBuyOrderList);

    GroupBuyOrderList queryByUserIdAndOutTradeNo(@Param("userId") String userId, @Param("outTradeNo") String outTradeNo);

    GroupBuyOrderList queryByUserIdAndOutTradeNoForUpdate(@Param("userId") String userId, @Param("outTradeNo") String outTradeNo);

    GroupBuyOrderList queryByPayNo(@Param("payNo") String payNo);

    List<String> queryCompleteOutTradeNoListByTeamId(@Param("teamId") String teamId);

    Integer queryUserOrderCount(@Param("activityId") Long activityId, @Param("userId") String userId);

    int updatePaymentRequest(GroupBuyOrderList groupBuyOrderList);

    int updateOrderStatusComplete(GroupBuyOrderList groupBuyOrderList);

    int updateOrderStatusClose(GroupBuyOrderList groupBuyOrderList);

}
