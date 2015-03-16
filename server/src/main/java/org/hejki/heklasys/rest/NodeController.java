package org.hejki.heklasys.rest;

import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.model.Node;
import org.hejki.heklasys.model.PinSetting;
import org.hejki.heklasys.model.msg.settings.GetNodeSettingsRequestMessage;
import org.hejki.heklasys.model.msg.settings.GetPinSettingsRequestMessage;
import org.hejki.heklasys.repository.NodeRepository;
import org.hejki.heklasys.repository.PinSettingRepository;
import org.hejki.heklasys.service.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Slf4j
@RestController
@RequestMapping("nodes")
public class NodeController {
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private PinSettingRepository pinSettingRepository;

    @Autowired
    private MessageSender sender;

    @RequestMapping(method = GET)
    public Page<Node> nodes(Pageable pageable) {
        return nodeRepository.findAll(pageable);
    }

    @RequestMapping(value = "{id}", method = GET)
    public Node nodeDetail(Integer id) {
        return nodeRepository.findOne(id);
    }

    @RequestMapping(value = "config", method = GET)
    public Object getConfig(@RequestParam("address") String address) {
        Node node = nodeRepository.findByAddress(address);
        return getNodeSettings(node);
    }

    @RequestMapping(value = "configUpdate", method = POST)
    public Node update(@RequestParam("address") String address) {
        Node node = nodeRepository.findByAddress(address);
        GetNodeSettingsRequestMessage.Response settings = getNodeSettings(node);

        int interval = settings.getSendingInterval();
        if (settings.getSendingIntervalUnit() == GetNodeSettingsRequestMessage.TimeUnit.MINUTES) {
            interval *= 60;
        } else if (settings.getSendingIntervalUnit() == GetNodeSettingsRequestMessage.TimeUnit.HOURS) {
            interval *= 60 * 60;
        }
        node.setUpdateInterval(interval);
        nodeRepository.save(node);

        return node;
    }

    @RequestMapping(value = "pinSettings", method = GET)
    public List<GetPinSettingsRequestMessage.Response> getPinSettings(@RequestParam("address") String address) {
        Node node = nodeRepository.findByAddress(address);

        log.debug("Check node={} pin settings", node);
        return getPinSettings(node);
    }

    @RequestMapping(value = "pinSettingsUpdate", method = POST)
    public Object updatePinSettings(@RequestParam("address") String address) {
        Node node = nodeRepository.findByAddress(address);

        log.debug("Update node={} pin settings", node);
        for (GetPinSettingsRequestMessage.Response response : getPinSettings(node)) {
            PinSetting setting = pinSettingRepository.findByNodeAndPinIndex(node.getId(), response.getPinIndex());
            if (null == setting) {
                setting = new PinSetting();
                setting.setNode(node);
                setting.setPinIndex(response.getPinIndex());
            }

            setting.setPinNumber(response.getPinNumber());
            setting.setType(response.getType());
            setting.setConfiguration(response.getConfig());
            pinSettingRepository.save(setting);
        }

        return "OK";
    }

    private List<GetPinSettingsRequestMessage.Response> getPinSettings(Node node) {
        List<GetPinSettingsRequestMessage.Response> result = new ArrayList<>(PinSetting.MAX_SETTINGS_COUNT);

        for (int i = 0; i < PinSetting.MAX_SETTINGS_COUNT; i++) {
            GetPinSettingsRequestMessage request = new GetPinSettingsRequestMessage(i);

            result.add(sender.sendAndReceive(node, request));
        }
        return result;
    }

    private GetNodeSettingsRequestMessage.Response getNodeSettings(Node node) {
        GetNodeSettingsRequestMessage request = new GetNodeSettingsRequestMessage();
        GetNodeSettingsRequestMessage.Response response = sender.sendAndReceive(node, request);

        return response;
    }
}
