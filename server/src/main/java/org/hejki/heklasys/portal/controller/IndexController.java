package org.hejki.heklasys.portal.controller;

import org.hejki.heklasys.core.model.devices.NodeDevice;
import org.hejki.heklasys.core.repository.DeviceRepository;
import org.hejki.heklasys.msg.impl.AnalogReadMessage;
import org.hejki.heklasys.msg.impl.MessageFrame;
import org.hejki.heklasys.msg.impl.PingMessage;
import org.hejki.heklasys.msg.model.Message;
import org.hejki.heklasys.msg.model.MessageType;
import org.hejki.heklasys.msg.repository.MessageRepository;
import org.hejki.heklasys.msg.service.MessageCommunicationService;
import org.hejki.heklasys.portal.model.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Controller
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MessageCommunicationService messageCommunicationService;

    @RequestMapping("/")
    public String homePage(Model model) {
        //        return "dashboard";
        return "redirect:/debug";
    }

    @RequestMapping("/debug")
    public String debugPage(Model model) {
        //        Page<Device> nodes = deviceRepository.findAllNodes(new PageRequest(0, Integer.MAX_VALUE));
        //        model.addAttribute("nodes", nodes.getContent());

        MessageDto message = new MessageDto();
        model.addAttribute("message", message);
        model.addAttribute("types", new MessageType[] {
                MessageType.PING_REQUEST, MessageType.ANALOG_READ
        });

        PageRequest pageable = new PageRequest(0, 10, Sort.Direction.DESC, "id");
        Iterable<Message> messages = messageRepository.findAll(pageable);
        model.addAttribute("messages", messages);
        return "dashboard";
    }

    @RequestMapping("/debug/sendMessage")
    public String debugSendMessage(MessageDto message) {
        MessageFrame messageFrame = null;

        switch (message.getType()) {
        case PING_REQUEST:
            messageFrame = new PingMessage(message.getIdentifier());
            break;
        case ANALOG_READ:
            messageFrame = new AnalogReadMessage(message.getIdentifier(), AnalogReadMessage.AnalogPin.valueOf(message.getData()));
        }

        NodeDevice node = deviceRepository.findNodeByIp(message.getIp());

        messageCommunicationService.send(messageFrame, node);
        return "redirect:/debug";
    }
}
