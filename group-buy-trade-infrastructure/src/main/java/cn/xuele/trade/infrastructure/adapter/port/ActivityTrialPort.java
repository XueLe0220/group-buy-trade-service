package cn.xuele.trade.infrastructure.adapter.port;

import cn.xuele.activity.api.IActivityTrialService;
import cn.xuele.activity.api.dto.ActivityTrialRequestDTO;
import cn.xuele.activity.api.dto.ActivityTrialResponseDTO;
import cn.xuele.common.types.enums.ResponseCode;
import cn.xuele.common.types.exception.AppException;
import cn.xuele.common.types.response.Response;
import cn.xuele.trade.domain.adapter.port.IActivityTrialPort;
import cn.xuele.trade.domain.model.entity.ActivityTrialEntity;
import cn.xuele.trade.domain.model.entity.command.TradeLockCommandEntity;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * activity-service 试算防腐适配器。
 *
 * @author XueLe
 * @version 1.0.0
 * @since 2026/06/18
 */
@Component
public class ActivityTrialPort implements IActivityTrialPort {

    @DubboReference(check = false, timeout = 3000)
    private IActivityTrialService activityTrialService;

    @Override
    public ActivityTrialEntity trial(TradeLockCommandEntity command) {
        ActivityTrialRequestDTO request = ActivityTrialRequestDTO.builder()
                .userId(command.getUserId())
                .goodsId(command.getGoodsId())
                .activityId(command.getActivityId())
                .source(command.getSource())
                .channel(command.getChannel())
                .build();

        Response<ActivityTrialResponseDTO> response = activityTrialService.trial(request);
        if (response == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "活动试算响应为空");
        }
        if (!response.isSuccess()) {
            throw new AppException(response.getCode(), response.getInfo());
        }

        ActivityTrialResponseDTO data = response.getData();
        if (data == null) {
            throw new AppException(ResponseCode.UN_ERROR.getCode(), "活动试算数据为空");
        }

        return ActivityTrialEntity.builder()
                .activityId(data.getActivityId())
                .activityName(data.getActivityName())
                .goodsId(data.getGoodsId())
                .goodsName(data.getGoodsName())
                .originalPrice(data.getOriginalPrice())
                .deductionPrice(data.getDeductionPrice())
                .payPrice(data.getPayPrice())
                .targetCount(data.getTargetCount())
                .validTime(data.getValidTime())
                .takeLimitCount(data.getTakeLimitCount())
                .startTime(data.getStartTime())
                .endTime(data.getEndTime())
                .visible(data.getVisible())
                .enable(data.getEnable())
                .build();
    }
}
