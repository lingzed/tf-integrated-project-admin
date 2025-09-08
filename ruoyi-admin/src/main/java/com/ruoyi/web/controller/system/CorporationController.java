package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.MsgConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.CorporationService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/corp")
@Validated
public class CorporationController extends BaseController {
    @Resource
    private CorporationService corporationService;
    @Resource
    private ISysUserService iSysUserService;

    /**
     * 公司选项列表
     * @return
     */
    @GetMapping("/option")
    public AjaxResult corpOptionList(@Max(value = 2, message = "未知的corpLevel")
                                       @Min(value = 0, message = "未知的corpLevel")
                                       @NotNull(message = "corpLevel 不能为空")
                                       Integer corpLevel) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (user.isAdmin()) {
            return success(corporationService.findByCorpLevel(corpLevel));
        }
        List<SysRole> roles = user.getRoles();  // 角色列表
        boolean hasAdmin = roles != null && roles.stream()
                .filter(role -> UserConstants.ROLE_NORMAL.equals(role.getStatus()))
                .anyMatch(role -> Constants.SUPER_ADMIN.equals(role.getRoleKey()));
        if (hasAdmin) {
            return success(corporationService.findByCorpLevel(corpLevel));
        } else {
            return success(corporationService.findByUserIdAndCorpLevel(user.getUserId(), corpLevel));
        }
    }

    /**
     * 分配用户的公司选项
     * @param corpId
     * @return
     */
    @PostMapping("/option/{corpId}")
    @PreAuthorize("@ss.hasAnyRoles('admin,gAdmin')")
    public AjaxResult assignCorpOption(Long userId, @PathVariable List<Integer> corpId) {
        SysUser sysUser = iSysUserService.selectUserById(userId);
        if (sysUser == null) {
            throw new ServiceException(MsgConstants.USER_NOT_EXISTS);
        }
        corporationService.assignCorpOption(userId, corpId);
        return success();
    }

    /**
     * 删除用户的公司选项
     * @param userId
     * @param corpId
     * @return
     */
    @DeleteMapping("/option/{corpId}")
    @PreAuthorize("@ss.hasAnyRoles('admin,gAdmin')")
    public AjaxResult delOption(Long userId, @PathVariable List<Integer> corpId) {
        SysUser sysUser = iSysUserService.selectUserById(userId);
        if (sysUser == null) {
            throw new ServiceException(MsgConstants.USER_NOT_EXISTS);
        }
        corporationService.delOption(userId, corpId);
        return success();
    }

    /**
     * 加载所有公司列表
     * @return
     */
    @GetMapping
    @PreAuthorize("@ss.hasAnyRoles('admin,gAdmin')")
    public AjaxResult loadCorpList() {
        return success(corporationService.findAll());
    }
}
