package com.ruoyi.framework.security.filter;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SSE临时票据认证
 */
@Component
public class SseTicketAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private RedisCache redisCache;
    @Resource
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 只处理带 sseTicket 的请求
        String ticket = request.getParameter("sseTicket");

        if (StringUtils.isNotEmpty(ticket)) {
            String ticketKey = CacheConstants.SSE_TICKET_CACHE_KEY + ticket;
            String userKey = redisCache.getCacheStr(ticketKey);
            if (userKey != null) {
                // 从缓存中获取 LoginUser
                LoginUser loginUser = redisCache.getCacheObject(userKey);
                if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
                    // 校验token
                    tokenService.verifyToken(loginUser);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // 使用一次后立即移除票据（一次性）
                    redisCache.deleteObject(ticketKey);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
