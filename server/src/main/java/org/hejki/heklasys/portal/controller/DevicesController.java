package org.hejki.heklasys.portal.controller;

import org.hejki.heklasys.core.model.Device;
import org.hejki.heklasys.core.repository.DeviceRepository;
import org.hejki.heklasys.msg.model.Message;
import org.hejki.heklasys.msg.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Controller
@RequestMapping("/devices")
public class DevicesController {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private MessageRepository messageRepository;

    @RequestMapping
    public String index(Model model, Pageable pageable) {
        Page<Device> devices = deviceRepository.findAll(pageable);

        model.addAttribute("devices", devices);
        return "devices/index";
    }

    @RequestMapping("{id}")
    public String deviceDetail(@PathVariable("id") int id, Model model) {
        Device device = deviceRepository.findOne(id);

        model.addAttribute("device", device);
        return "devices/detail";
    }

    @RequestMapping("{id}/ajax/messages")
    public String messages(@PathVariable("id") int deviceId, Model model) {
        Pageable pageable = new PageRequest(0, 10);
        Page<Message> messages = messageRepository.findByDeviceId(deviceId, pageable);

        model.addAttribute("messages", messages.getContent());
        return "devices/messagesTable";
    }
}
