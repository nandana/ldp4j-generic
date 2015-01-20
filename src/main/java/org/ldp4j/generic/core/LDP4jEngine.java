package org.ldp4j.generic.core;

import java.util.List;

public class LDP4jEngine {

    private List<Handler> handlerChain;

    private int counter = 0;

    private String lastHandlerName;

    public void serve(LDPContext context) throws LDPFault {

        for (Handler handler : handlerChain) {
            HandlerResponse response = handler.invoke(context);
            lastHandlerName = handler.getName();
            counter++;
            if(HandlerResponse.ABORT.equals(response)){
                break;
            }
        }
    }

    public void resume(LDPContext context) throws LDPFault {

        List<Handler> remainingHandlerChain = handlerChain.subList(counter, handlerChain.size());

        for (Handler handler : remainingHandlerChain) {
            HandlerResponse response = handler.invoke(context);
            lastHandlerName = handler.getName();
            counter++;
            if(HandlerResponse.ABORT.equals(response)){
                break;
            }
        }


    }

    public void setHandlerChain(List<Handler> handlers){
        handlerChain = handlers;
    }

    public String getLastHandlerName(){
        return lastHandlerName;
    }

}
