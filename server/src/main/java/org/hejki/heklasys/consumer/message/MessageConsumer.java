package org.hejki.heklasys.consumer.message;

import org.hejki.heklasys.model.Message;
import reactor.event.Event;
import reactor.function.Consumer;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal <petr.hejkal@doxologic.com>
 */
public interface MessageConsumer<T extends Message> extends Consumer<Event<T>> {
}
