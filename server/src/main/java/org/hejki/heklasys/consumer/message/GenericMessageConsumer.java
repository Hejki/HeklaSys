package org.hejki.heklasys.consumer.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hejki.heklasys.model.Message;
import org.hejki.heklasys.model.Node;
import org.hejki.heklasys.repository.NodeRepository;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.GenericTypeResolver;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.selector.Selectors;

import javax.annotation.PostConstruct;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
@Slf4j
public abstract class GenericMessageConsumer<T extends Message> implements MessageConsumer<T> {
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private NodeRepository nodeRepository;

    @Autowired
    @Qualifier("rootReactor")
    private Reactor reactor;

    @PostConstruct
    private void registerConsumer() {
        Class<?> selectorType = GenericTypeResolver.resolveTypeArgument(getClass(), GenericMessageConsumer.class);

        if (null != selectorType) {
            reactor.on(Selectors.type(selectorType), this);
        } else {
            throw new BeanCreationException("Cannot resolve generic type for bean class " + getClass().getName());
        }
    }

    @Override
    public void accept(Event<T> event) {
        T message = event.getData();

        if (log.isDebugEnabled()) {
            log.debug("Receive message={}, event_headers={}, event_key={}, event_id={}",
                    message, event.getHeaders(), event.getKey(), event.getId());
        }

        Node node = nodeRepository.findByAddress(event.getHeaders().getOrigin());
        if (null == node) {
            log.warn("Cannot process message from unknown node. message={}, event_headers={}, event_key={}, event_id={}",
                    message, event.getHeaders(), event.getKey(), event.getId());
            return;
        }
        handleMessage(message, node);
    }

    protected abstract void handleMessage(T message, Node node);
}
