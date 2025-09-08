package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.RepeatSubmit;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.web.service.TokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * SSE临时票据控制器
 */
@RestController
@RequestMapping("/sse")
public class SseTicketController extends BaseController {
    @Resource
    private TokenService tokenService;
    @Resource
    private RedisCache redisCache;

    @GetMapping("/ticket")
    public AjaxResult getTicket(HttpServletRequest request) {
        // 当前请求拿到userKey
        String userKey = tokenService.getUserKey(request);
        String ticket = IdUtils.fastSimpleUUID();   // 生成票据
        String ticketKey = CacheConstants.SSE_TICKET_CACHE_KEY + ticket;
        redisCache.setCacheStr(ticketKey, userKey); // 缓存票据
        redisCache.expire(ticketKey, 300);  // 设置票据过期时间300s
        Map<String, String> result = new HashMap<>();
        result.put("ticket", ticket);
        return success(result);
    }
}
