package cn.xuele.trade.infrastructure.dao;

import cn.xuele.trade.infrastructure.dao.po.GroupBuyOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 拼团队伍订单 DAO。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@Mapper
public interface IGroupBuyOrderDao {

    int insert(GroupBuyOrder groupBuyOrder);

    int updateAddLockCount(@Param("teamId") String teamId);

    int updateAddCompleteCount(@Param("teamId") String teamId);

    int updateTeamStatusComplete(@Param("teamId") String teamId);

    int updateUnpaidRefund(@Param("teamId") String teamId);

    int updatePaidUnformedRefund(@Param("teamId") String teamId);

    int updatePaidFormedRefund(@Param("teamId") String teamId, @Param("status") Integer status);

    GroupBuyOrder queryByTeamId(@Param("teamId") String teamId);

    GroupBuyOrder queryByTeamIdForUpdate(@Param("teamId") String teamId);

}
