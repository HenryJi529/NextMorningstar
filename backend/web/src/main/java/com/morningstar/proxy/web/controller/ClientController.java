package com.morningstar.proxy.web.controller;

import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.proxy.pojo.po.Node;
import com.morningstar.proxy.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

@Tag(name = "代理对客相关接口定义")
@RestController
@RequestMapping("/proxy/client")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "聚合节点")
    @GetMapping("/agg")
    public String agg(@RequestParam("token") String token) {
        List<Node> nodes = clientService.getAssociatedNodes(token);
        if (nodes == null) {
            return new BaseException(ResponseCode.PROXY_TOKEN_INVALID, token).getMessage();
        }
        List<String> links = nodes.stream().map(Node::getLink).toList();
        return Base64.getEncoder().encodeToString(String.join("\n", links).getBytes());
    }
}
