package com.morningstar.proxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.morningstar.exception.BaseException;
import com.morningstar.proxy.dao.mapper.NodeMapper;
import com.morningstar.proxy.lib.NodeParser;
import com.morningstar.proxy.pojo.bo.NodeDetail;
import com.morningstar.proxy.pojo.po.Node;
import com.morningstar.proxy.pojo.vo.req.CreateNodeRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateNodeRequestVo;
import com.morningstar.proxy.service.NodeService;
import com.morningstar.util.CopyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NodeServiceImpl implements NodeService {

    private final NodeMapper nodeMapper;

    private NodeDetail getNodeDetail(Node node) {
        NodeDetail nodeDetail = new NodeDetail();
        CopyUtil.copyNonNullProperties(node, nodeDetail);
        com.morningstar.proxy.lib.Node config = NodeParser.parse(node.getLink());
        nodeDetail.setName(config.getName());
        nodeDetail.setProtocol(config.getProtocol());
        return nodeDetail;
    }

    @Override
    public NodeDetail create(CreateNodeRequestVo vo) {
        Node node = Node.builder().link(vo.getLink()).subId(null).build();

        com.morningstar.proxy.lib.Node config = NodeParser.parse(node.getLink());
        if (config.getCountry() != null) {
            config.setName("Morningstar0_" + config.getCountry() + node.getId());
            node.setLink(config.format());
        }

        nodeMapper.insert(node);

        return getNodeDetail(node);
    }

    @Override
    public List<NodeDetail> getAll() {
        List<Node> nodeList = nodeMapper.selectList(null);
        if (nodeList == null || nodeList.isEmpty()) {
            return Collections.emptyList();
        }
        return nodeList.stream().map(this::getNodeDetail).toList();
    }

    @Override
    public void delete(Integer id) {
        int count = nodeMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("节点\"%d\"删除失败", id));
        }
    }

    @Override
    public NodeDetail update(UpdateNodeRequestVo vo) {
        Node node = nodeMapper.selectById(vo.getId());

        // 根据链接解析配置
        com.morningstar.proxy.lib.Node config = NodeParser.parse(vo.getLink());
        // 设置配置名称
        config.setName(vo.getName());
        // 设置节点配置链接
        node.setLink(config.format());
        // 一旦手动修改节点，subId只能为空
        LambdaUpdateWrapper<Node> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Node::getSubId, null)
                .eq(Node::getId, node.getId());

        int count = nodeMapper.update(node, lambdaUpdateWrapper);
        if (count != 1) {
            throw new BaseException(String.format("节点\"%d\"更新失败", vo.getId()));
        }

        return getNodeDetail(nodeMapper.selectById(vo.getId()));
    }

    @Override
    public NodeDetail get(Integer id) {
        Node node = nodeMapper.selectById(id);
        return getNodeDetail(node);
    }
}
