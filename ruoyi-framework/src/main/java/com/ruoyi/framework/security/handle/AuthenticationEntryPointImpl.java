package com.ruoyi.framework.security.handle;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 认证失败处理类 返回未授权
 *
 * @author ruoyi
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("text/event-stream")) {
            response.setHeader("content-type", "text/event-stream");
            response.setCharacterEncoding("UTF-8");
            String sseData = "data: " + "{\"type\":\"error\",\"message\":\"" + msg + "\"}" + "\n\n";
            try (OutputStream out = response.getOutputStream()) {
                out.write(sseData.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
        }
    }
}
