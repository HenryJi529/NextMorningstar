package com.morningstar.common.web.controller;

import com.morningstar.common.pojo.vo.req.EmailContactRequestVo;
import com.morningstar.common.service.ContactService;
import com.morningstar.constant.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "联系站长相关接口定义")
@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContactController {
    private final ContactService contactService;

    @Operation(summary = "获取评论(根据文章)")
    @PostMapping("/email")
    public R<Object> contactByEmail(@Valid @RequestBody EmailContactRequestVo vo) {
        contactService.contactByEmail(vo);
        return R.ok();
    }
}
