package com.morningstar.proxy.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.proxy.dao.mapper.NodeMapper;
import com.morningstar.proxy.dao.mapper.TokenMapper;
import com.morningstar.proxy.pojo.po.Node;
import com.morningstar.proxy.pojo.po.Token;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

@Tag(name = "代理对外相关接口定义")
@RestController
@RequestMapping("/proxy")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class GuestController {
    private final TokenMapper tokenMapper;
    private final NodeMapper nodeMapper;

    @Operation(summary = "节点聚合")
    @GetMapping("/agg")
    public String agg(@RequestParam("token") String token) {
        LambdaQueryWrapper<Token> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Token::getValue, token);

        if (tokenMapper.selectOne(queryWrapper) != null) {
            List<Node> nodes = nodeMapper.selectList(null);
            List<String> links = nodes.stream().map(Node::getLink).toList();

            return Base64.getEncoder().encodeToString(String.join("\n", links).getBytes());
        } else {
            return "令牌错误";
        }
    }
}
