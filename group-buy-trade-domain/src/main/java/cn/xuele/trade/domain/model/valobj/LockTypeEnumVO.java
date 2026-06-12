package cn.xuele.trade.domain.model.valobj;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 锁单类型 enum
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/12 17:05
 */
@Getter
@RequiredArgsConstructor
public enum LockTypeEnumVO {
    NEW_TEAM("开新团"),
    JOIN_TEAM("加入已有团");

    private final String info;
}
