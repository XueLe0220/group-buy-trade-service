package cn.xuele.trade.domain.model.valobj;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 拼团队伍状态 enum
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 15:03
 */
@Getter
@RequiredArgsConstructor
public enum GroupBuyTeamStatusEnumVO {

    PROGRESS(0, "拼团中"),
    COMPLETE(1, "已成团"),
    FAIL(2, "拼团失败"),
    PARTIAL_REFUND(3, "成团后部分退款")
    ;
    private final int status;
    private final String info;
}
