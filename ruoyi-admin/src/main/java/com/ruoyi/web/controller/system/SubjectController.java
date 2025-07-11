package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.u8c.subj.Subject;
import com.ruoyi.common.u8c.subj.SubjectVo;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.SubjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("/subject")
public class SubjectController extends BaseController {
    @Resource
    private SubjectService subjectService;

    /**
     * 导出科目列表数据
     * @param response
     */
    @GetMapping("/export")
    public void exportSubject(HttpServletResponse response) {
        Set<Subject> allSubject = subjectService.getAllSubjSet();
        ExcelUtil<Subject> util = new ExcelUtil<>(Subject.class);
        util.exportExcel(response, new ArrayList<>(allSubject), "科目列表");
    }

    /**
     * 手动刷新缓存
     * @return
     */
    @GetMapping("/refresh")
    public AjaxResult refreshAllSubjCache() {
        subjectService.refreshAllSubjCache();
        return success();
    }

    /**
     * 从缓存中获取具体的科目
     * @param subjCode
     * @return
     */
    @GetMapping("/{subj-code}")
    public AjaxResult getSubject(@PathVariable("subj-code") String subjCode) {
        Subject subject = subjectService.getSubject(subjCode);
        if (subject == null) return success();

        SubjectVo subjectVo = new SubjectVo();
        BeanUtils.copyProperties(subject, subjectVo);
        subjectVo.setDirectionCode(subject.getSubjDirection().getCode());
        subjectVo.setDirection(subject.getSubjDirection().getDirection());
        return success(subjectVo);
    }
}
