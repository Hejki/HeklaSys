//package org.hejki.heklasys.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.hejki.heklasys.model.Message;
//import org.hejki.heklasys.model.msg.response.PinValueMessage;
//import reactor.event.Event;
//import reactor.spring.context.annotation.Selector;
//
//import static org.hejki.heklasys.config.ReactorConfig.REACTOR_REF;
//import static reactor.spring.context.annotation.SelectorType.TYPE;
//
///**
// * TODO Document me.
// *
// * @author Petr Hejkal
// */
//@Slf4j
////@Consumer
//public class ResponseMessageProcessor {
//
//
//    @Selector(value = "org.hejki.heklasys.model.msg.response.OkResponseMessage", type = TYPE, reactor = REACTOR_REF)
//    public void handleOkResponseMessage(Event<Message> event) {
//    }
//
//    @Selector(value = "org.hejki.heklasys.model.msg.response.ErrorResponseMessage", type = TYPE, reactor = REACTOR_REF)
//    public void handleErrorMessage(Event<Message> event) {
//    }
//
//    @Selector(value = "org.hejki.heklasys.model.msg.response.PinValueMessage", type = TYPE, reactor = REACTOR_REF)
//    public void handlePinValueMessage(Event<PinValueMessage> event) {
//
//    }
//}
