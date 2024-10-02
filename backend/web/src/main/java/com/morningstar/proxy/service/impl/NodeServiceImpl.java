package com.morningstar.proxy.service.impl;

import com.morningstar.constant.R;
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
    public R<NodeDetail> create(CreateNodeRequestVo vo) {
        Node node = Node.builder().link(vo.getLink()).subId(null).build();
        nodeMapper.insert(node);

        com.morningstar.proxy.lib.Node config = NodeParser.parse(node.getLink());
        if(config.getCountry() != null){
            config.setName("Morningstar0_" + config.getCountry() + node.getId());
            node.setLink(config.format());
            nodeMapper.insert(node);
        }

        return R.ok(getNodeDetail(node));
    }

    @Override
    public R<List<NodeDetail>> getAll() {
        List<Node> nodeList = nodeMapper.selectList(null);
        if(nodeList == null || nodeList.isEmpty()){
            return R.ok(Collections.emptyList());
        }
        List<NodeDetail> nodeDetailList = nodeList.stream().map(this::getNodeDetail).toList();
        return R.ok(nodeDetailList);
    }

    @Override
    public R<Object> delete(Integer id) {
        int count = nodeMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("节点\"%d\"删除失败", id));
        }
        return R.ok();
    }

    @Override
    public R<NodeDetail> update(UpdateNodeRequestVo vo) {

        Node node = nodeMapper.selectById(vo.getId());
        // 一旦手动修改节点，subId只能为空
        node.setSubId(null);
        if(vo.getLink() != null) {
            node.setLink(vo.getLink());
        }
        if(vo.getName() != null) {
            com.morningstar.proxy.lib.Node config = NodeParser.parse(node.getLink());
            config.setName(vo.getName());
            node.setLink(config.format());
        }

        int count = nodeMapper.updateById(node);
        if (count != 1) {
            throw new BaseException(String.format("订阅\"%d\"更新失败", vo.getId()));
        }
        return R.ok(getNodeDetail(nodeMapper.selectById(vo.getId())));
    }

    @Override
    public R<NodeDetail> get(Integer id) {
        Node node = nodeMapper.selectById(id);
        NodeDetail nodeDetail = getNodeDetail(node);
        return R.ok(nodeDetail);
    }
}
