package org.hejki.heklasys.consumer.message;

import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.model.Node;
import org.hejki.heklasys.model.NodeValue;
import org.hejki.heklasys.model.PinSetting;
import org.hejki.heklasys.model.msg.response.PinValueMessage;
import org.hejki.heklasys.repository.NodeIndexDataRepository;
import org.hejki.heklasys.repository.PinSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.spring.context.annotation.Consumer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Slf4j
@Consumer
public class PinValueMessageConsumer extends GenericMessageConsumer<PinValueMessage> {
    @Autowired
    private PinSettingRepository pinSettingRepository;

    @Override
    protected void handleMessage(PinValueMessage message, Node node) {

        PinSetting settings = pinSettingRepository.findByNodeAndPinIndex(node.getId(), message.getPinIndex());
        if (null == settings) {
            log.warn("Cannot find pin settings for node_id={}, message_pinIndex={}",
                    node.getId(), message.getPinIndex());
            return;
        }

        log.trace("Found node node={} and pinSettings={}", node, settings);
        switch (settings.getType()) {
        case TEMPERATURE:
            if (log.isTraceEnabled()) {
                log.trace("Store temperature value={} from node_id={}", message.getPinValue() / (double) 100, node.getId());
            }
            getNodeRepository().storeTemperature(node.getId(), message.getPinValue() / (double) 100);
            break;
        default:
            log.warn("Store value for type={}, is not implemented.", settings.getType());
        }
    }
}
